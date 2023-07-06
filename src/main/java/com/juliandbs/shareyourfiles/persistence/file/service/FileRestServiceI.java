package com.juliandbs.shareyourfiles.persistence.file.service;

import com.juliandbs.shareyourfiles.persistence.file.dto.*;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.*;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.FolderNotExistsException;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.ForbiddenFolderAccessException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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
 * This interface set the base methods of the FileRestService class.
 * @author JulianDbs
 */
public interface FileRestServiceI {

    ResponseEntity<Object> storeNewFile(Authentication authentication, NewFileDto newFile)
            throws InvalidNewFileRequestException,
            UnauthenticatedUserException,
            EmptyNewFileException,
            FilePasswordsDoesNotMatchException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FileUploadFailureException,
            FileIsNotValidException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            NewFileIsToBigException;

    ResponseEntity<byte[]> getFileResource(Authentication authentication, FileRequestDto fileRequest)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            InvalidFilePasswordException;

    ResponseEntity<Object> showFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidShowFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException;

    ResponseEntity<Object> hideFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidHideFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException;

    ResponseEntity<Object> setFilePassword(final Authentication authentication, final NewFilePasswordDto newFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordsDoesNotMatchException,
            FileAlreadyHavePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException;

    ResponseEntity<Object> changeFilePassword(final Authentication authentication, final ChangeFilePasswordDto changeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidOriginalPasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            FilePasswordsDoesNotMatchException,
            FileDoesNotHavePasswordException;

    ResponseEntity<Object> removeFilePassword(final Authentication authentication, final RemoveFilePasswordDto removeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidOriginalPasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            InvalidFilePasswordException,
            FileDoesNotHavePasswordException;

    ResponseEntity<Object> deleteFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidDeleteFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException;
}
