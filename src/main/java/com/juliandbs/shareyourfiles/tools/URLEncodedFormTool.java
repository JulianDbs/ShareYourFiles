package com.juliandbs.shareyourfiles.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

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
 * This class is used to convert objects into URLEncodedForm formatted String.
 * @author JulianDbs
 */
public abstract class URLEncodedFormTool {

    public static String objectToEncodedFormString(Object object) throws NullPointerException {
        if (object == null)
            throw new NullPointerException();
        return objectToURLEncodedString(object);
    }

    private static String objectToURLEncodedString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<>() {});
        LinkedList<String> keySet = new LinkedList<>(map.keySet());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keySet.size(); i++) {
                Object o = map.get(keySet.get(i));
                String string = (String) o;
                sb.append(keySet.get(i)).append("=").append(string);
                if (i != (keySet.size() - 1)) {
                    sb.append("&");
                }
        }
        return sb.toString();
    }

}
