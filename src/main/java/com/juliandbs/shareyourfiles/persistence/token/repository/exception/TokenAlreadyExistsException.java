package com.juliandbs.shareyourfiles.persistence.token.repository.exception;

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
 * This class represents an Exception thrown when the token repository try to add a new token in table with a user id and ip address that is already in use.
 * @author JulianDbs
 */
public final class TokenAlreadyExistsException extends Exception {

    /**
     * Empty class constructor.
     * By default, this exception contains the message 'Token already exists'.
     */
    public TokenAlreadyExistsException() {super("Token already exists");}

    public TokenAlreadyExistsException(final String message) {super(message);}

    public TokenAlreadyExistsException(final String message, final Throwable cause) {super(message, cause);}

    public TokenAlreadyExistsException(final Throwable cause) {super("Token already exists", cause);}

}
