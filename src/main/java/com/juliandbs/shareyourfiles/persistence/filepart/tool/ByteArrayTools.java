package com.juliandbs.shareyourfiles.persistence.filepart.tool;

import java.text.DecimalFormat;

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
 * This class is used as tool to build human-readable byte array size.
 * @author JulianDbs
 */
public class ByteArrayTools {
    private static final float ONE_KILOBYTE = 1024.0f;
    private static final float ONE_MEGABYTE = (ONE_KILOBYTE * ONE_KILOBYTE);
    private static final float ONE_GIGABYTE = (ONE_MEGABYTE * ONE_KILOBYTE);
    private static final String NEGATIVE_VALUE = "negative value";

    public static String formatLengthIntoReadableSize(Long size) throws NullPointerException {
        if (size == null)
            throw new NullPointerException();
        return buildReadableSize(size);
    }

    public static String formatLengthIntoReadableSize(Integer size) throws NullPointerException {
        if (size == null)
            throw new NullPointerException();
        return buildReadableSize(Long.valueOf(size));
    }

    private static String buildReadableSize(Long size) {
        String result;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (size >= 0) {
            result = decimalFormat.format(size / ONE_KILOBYTE) + " Kb";
            if (size == ONE_MEGABYTE|| size > ONE_MEGABYTE && size < ONE_GIGABYTE) {
                result = decimalFormat.format(size / ONE_MEGABYTE) + " Mb";
            }
            if (size == ONE_GIGABYTE || size > ONE_GIGABYTE) {
                result = decimalFormat.format(size / ONE_MEGABYTE) + " Gb";
            }
        } else {
            result = NEGATIVE_VALUE;
        }
        return result;
    }

    public static Integer getMegaByteCountInByteLength(Integer value) throws NullPointerException {
        if (value == null)
            throw new NullPointerException();
        float oneMegaByte = (ONE_KILOBYTE * ONE_KILOBYTE);
        return Float.valueOf(oneMegaByte * value).intValue();
    }
}
