package com.juliandbs.shareyourfiles.persistence.filepart.tool;

import com.juliandbs.shareyourfiles.persistence.file.dto.FilePartDto;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

/**
 * This class is used as a tool to convert file parts into a byte array or split a byte array into a list of file parts.
 * @author JulianDbs
 */
public class FilePartTools {

    public static byte[] joinFileParts(List<FilePartDto> filePartList) throws NullPointerException {
        if (filePartList == null)
            throw new NullPointerException();
        int totalLength = filePartList.stream()
                                        .mapToInt(filePart -> filePart.getFilePartData().length)
                                        .sum();
        byte[] result = new byte[0];
        if (totalLength > 0) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);
                for (FilePartDto filePart : filePartList) {
                    byteBuffer.put(filePart.getFilePartData());
                }
                result = byteBuffer.array();
                //clean data
                byteBuffer.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<FilePartDto> splitByteArrayIntoFilePartList(int chunkSize, byte[] byteArray) throws NullPointerException {
        if (byteArray == null)
            throw new NullPointerException();
        List<FilePartDto> list = new LinkedList<>();
        if (byteArray.length > 0) {
            int partCount = (byteArray.length / chunkSize) + 1;
            List<Integer> startPositionList = buildStartPositionList(chunkSize, partCount);
            List<Integer> partSizeList = buildByteArraySizeList(chunkSize, partCount, byteArray.length);
            for (int i = 0; i < partCount; i++) {
                Integer startPosition = startPositionList.get(i);
                Integer partSize = partSizeList.get(i);
                list.add( new FilePartDto( i, getByteArrayChunk( byteArray, startPosition, partSize) ) );
           }
            //clean data
            startPositionList.clear();
            partSizeList.clear();
        } else {
            list.add(new FilePartDto(0, byteArray));
        }
        return list;
    }

    private static byte[] getByteArrayChunk(byte[] byteArray, int startPosition, int partSize) {
        byte[] newChunk = new byte[partSize];
        System.arraycopy(byteArray, startPosition, newChunk, 0, partSize);
        return newChunk;
    }

    private static List<Integer> buildStartPositionList(int chunkSize, int partCount) {
        List<Integer> result = new LinkedList<>();
        for (int i = 0; i < partCount; i++) {
            if (i == (partCount - 1)) {
                result.add( (chunkSize * i) );
            } else {
                result.add( ( (chunkSize) * i ) );
            }
        }
        return result;
    }

    private static List<Integer> buildByteArraySizeList(int chunkSize, int partCount, int byteArrayLength) {
        List<Integer> result = new LinkedList<>();
        for (int i = 0; i < partCount; i++) {
            if (i == (partCount -1)) {
                result.add( (byteArrayLength - (chunkSize * i)) );
            } else {
                result.add(chunkSize);
            }
        }
        return result;
    }
}
