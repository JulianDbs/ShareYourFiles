package com.juliandbs.shareyourfiles.persistence.token.filter;

import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;
import com.juliandbs.shareyourfiles.persistence.token.service.TokenService;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidTokenException;
import com.juliandbs.shareyourfiles.persistence.token.tool.TokenTool;
import com.juliandbs.shareyourfiles.security.UserDetailsImpl;
import com.juliandbs.shareyourfiles.security.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
 * This class extends the OncePerRequestFilter class and is used as a filter to get the 'token' cookie, if it exists.
 * If the cookie exists and its content is not null, the content is decrypted using the TokenTool.
 * The token (the content of the cookie) is validated using the TokenService.
 * If the token is valid, his expiration date is extended and is used to authenticate the request.
 * If the token is not valid, a ServletException Exception is thrown.
 * @author JulianDbs
 */
public final class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE_NAME = "token";

    private final TokenService tokenService;

    private final UserDetailsServiceImpl userDetailsService;

    public TokenAuthenticationFilter(final TokenService tokenService, final UserDetailsServiceImpl userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        boolean containsCookie = TokenTool.requestContainsTokenCookie(request);
        if (containsCookie) {
            boolean valid = this.processToken(request);
            if (!valid)
                this.deleteTokenCookie(response);
        }
        filterChain.doFilter(request, response);
    }

    private void deleteTokenCookie(final HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private boolean processToken(final HttpServletRequest request) throws ServletException {
        String encryptedToken = TokenTool.getTokenCookieFromRequest(request);
        String token;
        try {
            token = this.tokenService.decryptToken(encryptedToken);
        } catch (InvalidTokenException e) {
            return false;
        }
        String ipAddress = request.getRemoteAddr();
        if (token == null || ipAddress == null)
            return false;
        boolean tokenExists = this.tokenService.tokenExists(token, ipAddress);
        if (!tokenExists)
            return false;
        boolean tokenIsValid = this.tokenService.tokenIsValid(token, ipAddress);
        if (tokenIsValid) {
            try {
                tokenService.refreshToken(token, ipAddress);
            } catch (InvalidTokenException e) {
                return false;
            }
            return this.authenticateFromToken(token, ipAddress, request);
        } else {
            try {
                UUID tokenId = UUID.fromString(token);
                this.tokenService.removeTokenByTokenAndIpAddress(tokenId, ipAddress);
            } catch (TokenNotFoundException e) {
                return false;
            }
            return false;
        }
    }

    private boolean authenticateFromToken(final String token, final String ipAddress, final HttpServletRequest request) {
        String userEmail;
        try {
            userEmail = tokenService.getTokenOwner(token, ipAddress);
        } catch (InvalidTokenException e) {
            return false;
        }
        UserDetailsImpl userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return true;
    }

}
