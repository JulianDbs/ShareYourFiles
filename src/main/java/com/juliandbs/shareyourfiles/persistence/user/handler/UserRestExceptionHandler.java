package com.juliandbs.shareyourfiles.persistence.user.handler;

import com.juliandbs.shareyourfiles.persistence.file.service.exception.InvalidOriginalPasswordException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.SamePasswordException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;
import com.juliandbs.shareyourfiles.persistence.user.service.exception.UsernameNotValidException;
import com.juliandbs.shareyourfiles.persistence.user.service.exception.*;
import com.juliandbs.shareyourfiles.tools.RestResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
 * This class is used to handle the exceptions throw by the UserRestService service.
 * @author JulianDbs
 */
@ControllerAdvice
public class UserRestExceptionHandler {

    private final MessageSource messageSource;

    public UserRestExceptionHandler(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<Object> handleUnauthenticatedUserException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.unauthorized-user");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidUserPasswordException.class)
    public ResponseEntity<Object> handleInvalidUserPasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.wrong-user-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotValidException.class)
    public ResponseEntity<Object> handleUsernameNotValidException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.username-not-valid");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordsDoesNotMatchException.class)
    public ResponseEntity<Object> handlePasswordsDoesNotMatchException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.password-does-not-match");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<Object> handleSamePasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.same-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOriginalPasswordException.class)
    public ResponseEntity<Object> handleInvalidOriginalPasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-original-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidChangeUsernameRequestException.class)
    public ResponseEntity<Object> handleInvalidChangeUsernameRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-change-account-username-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidChangeAccountPasswordRequestException.class)
    public ResponseEntity<Object> handleInvalidChangeAccountPasswordRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-change-account-password-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDeleteAccountRequestException.class)
    public ResponseEntity<Object> handleInvalidDeleteAccountRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-delete-account-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewPasswordIsTheSameException.class)
    public ResponseEntity<Object> handleNewPasswordIsTheSameException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-password-is-the-same");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUsernameIsTheSameException.class)
    public ResponseEntity<Object> handleNewUsernameIsTheSameException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-username-is-the-same");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUsernameIsToShortException.class)
    public ResponseEntity<Object> handleNewUsernameIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-username-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUsernameIsToLongException.class)
    public ResponseEntity<Object> handleNewUsernameIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-username-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPasswordIsToShortException.class)
    public ResponseEntity<Object> handleUserPasswordIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.user-password-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPasswordIsToLongException.class)
    public ResponseEntity<Object> handleUserPasswordIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.user-password-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNewPasswordIsToShortException.class)
    public ResponseEntity<Object> handleUserNewPasswordIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-user-password-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNewPasswordIsToLongException.class)
    public ResponseEntity<Object> handleUserNewPasswordIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-user-password-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDoesNotExistsException.class)
    public ResponseEntity<Object> handleUserDoesNotExistsException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.user-does-not-exists");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
