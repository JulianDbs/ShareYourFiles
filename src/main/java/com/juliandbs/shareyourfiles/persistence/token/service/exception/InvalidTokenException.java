package com.juliandbs.shareyourfiles.persistence.token.service.exception;

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
 * This class represents an Exception thrown when the token service receives an invalid token.
 * @author JulianDbs
 */
public final class InvalidTokenException extends Exception {

    /**
     * Empty class constructor.
     * By default, this exception contains the message 'Invalid token'.
     */
    public InvalidTokenException() {super("Invalid token");}

    public InvalidTokenException(final String message) {super(message);}

    public InvalidTokenException(final String message, final Throwable cause) {super(message, cause);}

    public InvalidTokenException(final Throwable cause) {super("Invalid token", cause);}

}
