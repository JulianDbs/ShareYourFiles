package com.juliandbs.shareyourfiles.persistence.user.service.exception;

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
 * This is the base exception used by the UserRestService.
 * @author JulianDbs
 */
public class UserRestException extends Exception {

    public UserRestException() {super("User rest exception");}

    public UserRestException(String message) {super(message);}

    public UserRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRestException(Throwable cause) {super(cause);}
}
