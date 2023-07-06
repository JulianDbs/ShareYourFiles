package com.juliandbs.shareyourfiles.persistence.token.service;

import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.FailedTokenCreationException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidTokenException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidUserEmailException;

import jakarta.servlet.ServletException;

import java.util.UUID;

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
 * This interface contains the base method for the TokenService class.
 * @author JulianDbs
 */
public interface TokenServiceI {

    String decryptToken(final String encryptedToken) throws NullPointerException, InvalidTokenException, ServletException;

    String encryptToken(final String token) throws NullPointerException, InvalidTokenException, ServletException;

    String createEncryptedToken(final String userEmail, final String ipAddress) throws NullPointerException, InvalidUserEmailException, FailedTokenCreationException;

    String getEncryptedTokenByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException, ServletException, InvalidTokenException;

    String getTokenOwner(final String token, final String ipAddress) throws NullPointerException, InvalidTokenException;

    void refreshToken(final String token, final String ipAddress) throws NullPointerException, InvalidTokenException;

    boolean tokenExists(final String token, final String ipAddress) throws NullPointerException;

    boolean tokenExists(final UUID userId, String ipAddress) throws NullPointerException;

    boolean tokenIsValid(final UUID userId, final String ipAddress) throws NullPointerException;

    boolean tokenIsValid(final String token, final String ipAddress) throws NullPointerException;

    void removeTokenByUserIdAndIpAddress(UUID userId, String ipAddress) throws NullPointerException, TokenNotFoundException;

    void removeTokenByTokenAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

}
