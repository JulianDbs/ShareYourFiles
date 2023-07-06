package com.juliandbs.shareyourfiles.persistence.folder.service;

import com.juliandbs.shareyourfiles.persistence.folder.dto.*;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.*;
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
 * This interface set the base methods of the FolderRestService class.
 * @author JulianDbs
 */
public interface FolderRestServiceI {

    ResponseEntity<Object> addNewFolder(Authentication authentication, NewFolderDto newFolderDto)
            throws UnauthenticatedUserException,
            FolderNameIsToLongException,
            FolderPasswordsDoesNotMatchException,
            InvalidAddNewFolderRequestException,
            FolderNameIsToShortException,
            FolderPasswordIsToLongException,
            ParentFolderNotExistsException,
            ForbiddenParentFolderAccessException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> changeFolderVisibility(final Authentication authentication, final FolderRequestDto folderRequest, final boolean toPublic)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidShowFolderRequestException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> setFolderPassword(final Authentication authentication, final NewFolderPasswordDto newFolderPassword)
            throws UnauthenticatedUserException,
            FolderAlreadyHavePasswordException,
            FolderPasswordsDoesNotMatchException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> changeFolderPassword(final Authentication authentication, final ChangeFolderPasswordDto changeFolderPassword)
            throws UnauthenticatedUserException,
            FolderPasswordsDoesNotMatchException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> removeFolderPassword(final Authentication authentication, final RemoveFolderPasswordDto removeFolderPassword)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> changeFolderName(final Authentication authentication, final NewFolderNameDto newFolderName)
            throws UnauthenticatedUserException,
            FolderNameIsToLongException,
            SameFolderNameException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            InvalidUpdateFolderNameRequestException,
            FolderNameIsToShortException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;

    ResponseEntity<Object> deleteFolder(final Authentication authentication, final DeleteFolderDto deleteFolder)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            InvalidDeleteFolderRequestException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException;
}
