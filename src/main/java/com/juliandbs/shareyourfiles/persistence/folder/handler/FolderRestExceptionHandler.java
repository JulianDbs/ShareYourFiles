package com.juliandbs.shareyourfiles.persistence.folder.handler;

import com.juliandbs.shareyourfiles.persistence.folder.service.exception.*;
import com.juliandbs.shareyourfiles.tools.RestResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
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
 * This class is used to handle the exceptions throw by the FolderRestService service.
 * @author JulianDbs
 */
@ControllerAdvice
public class FolderRestExceptionHandler {

    private final MessageSource messageSource;

    public FolderRestExceptionHandler(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler( FolderNotExistsException.class )
    public ResponseEntity<Object> handleFolderNotExistsException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder.does-not-exists");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidAddNewFolderRequestException.class )
    public ResponseEntity<Object> handleInvalidAddNewFolderRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-add-new-folder-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderNameIsToLongException.class )
    public ResponseEntity<Object> handleFolderNameIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-name-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderPasswordIsToLongException.class )
    public ResponseEntity<Object> handleFolderPasswordIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-password-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderNameIsToShortException.class )
    public ResponseEntity<Object> handleFolderNameIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-name-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderPasswordIsToShortException.class )
    public ResponseEntity<Object> handleFolderPasswordIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-password-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderPasswordsDoesNotMatchException.class )
    public ResponseEntity<Object> handleNewFolderPasswordsDoesNotMatchException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-passwords-does-not-match");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler( InvalidDeleteFolderRequestException.class )
    public ResponseEntity<Object> handleInvalidDeleteFolderRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-delete-folder-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParentFolderNotExistsException.class)
    public ResponseEntity<Object> handleParentFolderNotExistsException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.parent-folder-not-exists");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( ForbiddenFolderAccessException.class )
    public ResponseEntity<Object> handleForbiddenFolderAccessException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.forbidden-folder-access");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameFolderNameException.class)
    public ResponseEntity<Object> handleSameFolderNameException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.same-folder-name");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( ForbiddenParentFolderAccessException.class )
    public ResponseEntity<Object> handleForbiddenParentFolderAccessException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.forbidden-parent-folder-access");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InterruptedFolderRequestException.class )
    public ResponseEntity<Object> handleInterruptedFolderRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.interrupted-folder-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidFolderPasswordException.class )
    public ResponseEntity<Object> handleInvalidFolderPasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-folder-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidShowFolderRequestException.class )
    public ResponseEntity<Object> handleInvalidShowFolderRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-show-folder-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( FolderAlreadyHavePasswordException.class )
    public ResponseEntity<Object> handleFolderAlreadyHavePasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.folder-already-have-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidFolderRequestException.class )
    public ResponseEntity<Object> handleInvalidFolderRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-folder-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( InvalidUpdateFolderNameRequestException.class )
    public ResponseEntity<Object> handleInvalidUpdateFolderNameRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-update-folder-name-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }
}