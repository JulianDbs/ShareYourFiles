package com.juliandbs.shareyourfiles.tools;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

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
 * This class is used to build a request response that contains a Java Map that can be received as a JSON object by Fetch JavaScript.
 * @author JulianDbs
 */
public abstract class RestResponse {

    public static ResponseEntity<Object> build(final String message, final HttpStatus status, final Object data) throws NullPointerException {
        if (message == null || status == null || data == null)
            throw new NullPointerException();
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", data);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(map);
    }

    public static ResponseEntity<Object> build(final String message, final HttpStatus status) throws NullPointerException {
        if (message == null || status == null)
            throw new NullPointerException();
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", "");
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(map);
    }
}
