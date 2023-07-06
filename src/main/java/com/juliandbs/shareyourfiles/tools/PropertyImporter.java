package com.juliandbs.shareyourfiles.tools;

import org.springframework.core.env.Environment;

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
 * This class is used as a tool to retrieve environment properties values.
 * @author JulianDbs
 */
public class PropertyImporter {

    public static Boolean getCanCreateAccountProperty(Environment environment) throws NullPointerException {
        if (environment == null)
            throw new NullPointerException();
        return getBooleanProperty(environment);
    }

    private static Boolean getBooleanProperty(Environment environment) {
        boolean result = true;
        if (!environment.containsProperty("canCreateNewAccounts")) {
            return result;
        }
        try {
            String property = environment.getProperty("canCreateNewAccounts");
            if (property == null) {
                return result;
            }
            if (!property.equalsIgnoreCase("true") && !property.equalsIgnoreCase("false")){
                return result;
            }
            return Boolean.parseBoolean(property);
        } catch (NullPointerException e) {
            return result;
        }
    }

    public static long getMaxFileSizeProperty(Environment environment) {
        if (!environment.containsProperty("app.file.max-file-size"))
            throw new RuntimeException("'app.file.max-file-size' property does not exists!!!!!");
        String property = environment.getProperty("app.file.max-file-size");
        if (property == null)
            throw new RuntimeException("'app.file.max-file-size' property is null");
        return Long.parseLong(property);
    }
}
