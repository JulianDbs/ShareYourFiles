package com.juliandbs.shareyourfiles.persistence.token.handler;

import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;
import com.juliandbs.shareyourfiles.persistence.token.service.TokenService;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidTokenException;
import com.juliandbs.shareyourfiles.persistence.token.tool.TokenTool;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
 * This class extends the SecurityContextLogoutHandler class and is used to handle the logout request.
 * @author JulianDbs
 */
public class LogoutHandler extends SecurityContextLogoutHandler {

    private static final String TOKEN_COOKIE_NAME = "token";

    private final TokenService tokenService;

    public LogoutHandler(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        boolean exists = TokenTool.requestContainsTokenCookie(request);
        if (exists) {
            String ipAddress = request.getRemoteAddr();
            String encryptedToken = TokenTool.getTokenCookieFromRequest(request);
            try {
                String token = this.tokenService.decryptToken(encryptedToken);
                boolean isValid = this.tokenService.tokenIsValid(token, ipAddress);
                if (isValid) {
                    UUID tokenId = UUID.fromString(token);
                    tokenService.removeTokenByTokenAndIpAddress(tokenId, ipAddress);
                    Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            } catch (InvalidTokenException | TokenNotFoundException | ServletException ignored) {}
        }
    }
}
