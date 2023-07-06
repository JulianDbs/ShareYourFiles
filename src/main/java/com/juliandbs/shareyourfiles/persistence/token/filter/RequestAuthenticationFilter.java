package com.juliandbs.shareyourfiles.persistence.token.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.juliandbs.shareyourfiles.persistence.token.filter.exception.InvalidRequestBodyException;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;
import com.juliandbs.shareyourfiles.persistence.token.service.TokenService;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.FailedTokenCreationException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidTokenException;
import com.juliandbs.shareyourfiles.persistence.token.service.exception.InvalidUserEmailException;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.security.UserDetailsImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
 *  This class extends the UsernamePasswordAuthenticationFilter class and is used as a filter to intercept the login
 *  process and implements the token service.
 * @author JulianDbs
 */
public final class RequestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String TOKEN_COOKIE_NAME = "token";

    private static final String LOGIN_REQUEST_PATH = "/login";

    private static final String DESKTOP_PATH = "/desktop";

    private final TokenService tokenService;

    public RequestAuthenticationFilter(final AuthenticationManager authenticationManager, final TokenService tokenService) {
        this.setAuthenticationSuccessHandler(this::loginSuccessHandler);
        this.setAuthenticationFailureHandler(this::loginFailureHandler);
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(LOGIN_REQUEST_PATH, "POST"));
        this.setAuthenticationManager(authenticationManager);
        this.tokenService = tokenService;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        String requestBody = this.getRequestBody(request);
        LoginRequestDto loginRequestDto = this.buildLoginRequestDto(requestBody);
        Authentication authentication = this.getAuthenticationByLoginRequestDto(loginRequestDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private String getRequestBody(final HttpServletRequest request) throws InvalidRequestBodyException {
        String requestBody;
        try {
            BufferedReader reader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ( (line = reader.readLine() ) != null) {
                stringBuilder.append(line);
            }
            requestBody = stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestBodyException("The character set encoding used in the request body is not supported and the text cannot be decoded.", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestBodyException("The getInputStream method already has been called on this request.", e);
        } catch (IOException e) {
            throw new InvalidRequestBodyException("Internal error.", e);
        }
        return URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
    }

    private LoginRequestDto buildLoginRequestDto(final String requestBody) throws InvalidRequestBodyException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto loginRequest;
        try {
            String jsonBody = this.requestBodyToJsonString(requestBody);
            loginRequest = objectMapper.readValue(jsonBody, LoginRequestDto.class);
        } catch (StreamReadException e) {
            throw new InvalidRequestBodyException("The request body contains invalid content of type JsonParser supports (JSON for default case)");
        } catch (DatabindException e) {
            throw new InvalidRequestBodyException("The input JSON structure does not match structure expected for the LoginRequestDto", e);
        } catch (JsonProcessingException e) {
            throw new InvalidRequestBodyException("The input JSON structure does not match structure expected for result type (or has other mismatch issues)" , e);
        }
        return loginRequest;
    }

    private String requestBodyToJsonString(final String requestBody) {
        String result = requestBody;
        result = result.replace("=", "\":\"").replace("&", "\",\"");
        return ( "{\"" + result + "\"}");
    }

    private Authentication getAuthenticationByLoginRequestDto(final LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        AuthenticationManager authenticationManager = this.getAuthenticationManager();
        return authenticationManager.authenticate(authenticationToken);
    }

    private String createNewToken(final Authentication authentication, final String ipAddress) throws InvalidUserEmailException, FailedTokenCreationException {
        String userEmail = this.getUserEmailFromAuthentication(authentication);
        return this.tokenService.createEncryptedToken(userEmail, ipAddress);
    }

    private String getExistingToken(final UUID userId, final String ipAddress) throws TokenNotFoundException, ServletException, InvalidTokenException {
        return this.tokenService.getEncryptedTokenByUserIdAndIpAddress(userId, ipAddress);
    }

    private UUID getUserIdFromAuthentication(final Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    private String getUserEmailFromAuthentication(final Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getEmailAddress();
    }

    private String processExistingToken(final Authentication authentication, final UUID userId, final String ipAddress) throws InvalidUserEmailException, FailedTokenCreationException {
        String encryptedToken;
        boolean isValid = this.tokenService.tokenIsValid(userId, ipAddress);
        if (isValid) {
            try {
                encryptedToken = this.getExistingToken(userId, ipAddress);
            } catch (TokenNotFoundException | ServletException | InvalidTokenException e) {
                encryptedToken = this.createNewToken(authentication, ipAddress);
            }
        } else {
            encryptedToken = this.createNewToken(authentication, ipAddress);
        }
        return encryptedToken;
    }

    private void loginSuccessHandler(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        response.setStatus(HttpStatus.OK.value());
        UUID userId = this.getUserIdFromAuthentication(authentication);
        String ipAddress = request.getRemoteAddr();
        boolean tokenAlreadyExists = this.tokenService.tokenExists(userId, ipAddress);
        String encryptedToken;
        try {
            if (tokenAlreadyExists) {
                encryptedToken = this.processExistingToken(authentication, userId, ipAddress);
            } else {
                encryptedToken = this.createNewToken(authentication, ipAddress);
            }
            response.addCookie(new Cookie(TOKEN_COOKIE_NAME, encryptedToken));
            response.sendRedirect(DESKTOP_PATH);
        } catch (InvalidUserEmailException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (FailedTokenCreationException | IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void loginFailureHandler(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authenticationException) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}

