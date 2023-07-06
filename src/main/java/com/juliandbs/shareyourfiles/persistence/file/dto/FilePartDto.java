package com.juliandbs.shareyourfiles.persistence.file.dto;

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
 * A class used as a data transfer object and carry data between process.
 * @author JulianDbs
 */
public class FilePartDto {

    private Integer filePartOrder;

    private byte[] filePartData;

    public FilePartDto() {}

    public FilePartDto(Integer filePartOrder, byte[] filePartData) throws NullPointerException {
        if (filePartOrder == null || filePartData == null)
            throw new NullPointerException();
        this.filePartOrder = filePartOrder;
        this.filePartData = filePartData;
    }

    public Boolean isValid() { return (filePartOrder != null && filePartData != null);}

    public Integer getFilePartOrder() {
        return filePartOrder;
    }

    public void setFilePartOrder(Integer filePartOrder) throws NullPointerException {
        if(filePartOrder == null)
            throw new NullPointerException();
        this.filePartOrder = filePartOrder;
    }

    public byte[] getFilePartData() {
        return filePartData;
    }

    public void setFilePartData(byte[] filePartData) throws NullPointerException {
        if(filePartData == null)
            throw new NullPointerException();
        this.filePartData = filePartData;
    }

    @Override
    public int hashCode() {return filePartOrder.hashCode();}

    @Override
    public String toString() {return filePartOrder.toString();}
}
