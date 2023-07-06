package com.juliandbs.shareyourfiles.persistence.file.handler;

import com.juliandbs.shareyourfiles.persistence.file.service.exception.*;
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
 * This class handles the exceptions thrown by the FileRestService.
 * @author JulianDbs
 */
@ControllerAdvice
public class FileRestExceptionHandler {

    private final MessageSource messageSource;

    public FileRestExceptionHandler(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(NewFileIsToBigException.class)
    public ResponseEntity<Object> handleNewFileIsToBigException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.new-file-is-to-big");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidNewFileRequestException.class)
    public ResponseEntity<Object> handleInvalidNewFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-new-file-request-dto");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilePasswordIsToShortException.class)
    public ResponseEntity<Object> handleFilePasswordIsToShortException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-password-is-to-short");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilePasswordIsToLongException.class)
    public ResponseEntity<Object> handleFilePasswordIsToLongException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-password-is-to-long");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilePasswordsDoesNotMatchException.class)
    public ResponseEntity<Object> handleFilePasswordDoesNotMatchException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-passwords-does-not-match");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyNewFileException.class)
    public ResponseEntity<Object> handleInvalidNewFileDataException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.empty-new-file");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadFailureException.class)
    public ResponseEntity<Object> handleFileUploadFailureException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-upload-failure");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileIsNotValidException.class)
    public ResponseEntity<Object> handleFileIsNotValidException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-is-not-valid");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileRequestException.class)
    public ResponseEntity<Object> handleInvalidFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-file-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotExistsException.class)
    public ResponseEntity<Object> handleFileNotExistsException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-does-not-exists");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenFileAccessException.class)
    public ResponseEntity<Object> handleForbiddenFileAccessException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-forbidden");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InterruptedFileRequestException.class)
    public ResponseEntity<Object> handleInterruptedFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.interrupted-file-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileAlreadyHavePasswordException.class)
    public ResponseEntity<Object> handleFileAlreadyHavePasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-already-have-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOriginalPasswordException.class)
    public ResponseEntity<Object> handleInvalidOriginalPasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-original-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidShowFileRequestException.class)
    public ResponseEntity<Object> handleShowFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-show-file-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidHideFileRequestException.class)
    public ResponseEntity<Object> handleHideFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-hide-file-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFilePasswordException.class)
    public ResponseEntity<Object> handleInvalidFilePasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-file-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDeleteFileRequestException.class)
    public ResponseEntity<Object> handleDeleteFileRequestException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.invalid-delete-file-request");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileDoesNotHavePasswordException.class)
    public ResponseEntity<Object> handleFileDoesNotHavePasswordException() {
        String errorMessage = this.getLocalizedMessage("lang.exception.file-does-not-have-password");
        return RestResponse.build(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
