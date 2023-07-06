package com.juliandbs.shareyourfiles.persistence.base;

import com.juliandbs.shareyourfiles.persistence.file.dto.FileDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.NewFileDto;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.ForbiddenFileAccessException;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;
import com.juliandbs.shareyourfiles.security.UserDetailsImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

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
 * This class is used as a base service and contains the folder, file and fileparts repositories.
 * @author JulianDbs
 */
public class BaseService {

    private final MessageSource messageSource;

    protected final FolderRepository folderRepository;

    protected final FileRepository fileRepository;

    protected final FilePartsRepository filePartsRepository;


    public BaseService(final MessageSource messageSource, final FolderRepository folderRepository, final FileRepository fileRepository, final FilePartsRepository filePartsRepository) {
        this.messageSource = messageSource;
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.filePartsRepository = filePartsRepository;
    }

    protected void validateRestAuthentication(final Authentication authentication) throws UnauthenticatedUserException {
        if (authentication == null || !authentication.isAuthenticated())
            throw new UnauthenticatedUserException();
    }

    protected String getLocalizedMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected FileDto buildFileDto(String emailAddress, UUID folderId, NewFileDto newFile) {
        String fileName = newFile.getFile().getOriginalFilename();
        String fileContentType = newFile.getFile().getContentType();
        Long fileSize = newFile.getFile().getSize();
        String password = "";
        if (newFile.getFileHavePassword()) {
            password = this.passwordEncoder().encode(newFile.getPassword());
        }
        return new FileDto(emailAddress,
                folderId,
                fileName,
                fileName,
                fileContentType,
                fileSize,
                newFile.getFileIsPrivate(),
                newFile.getFileHavePassword(),
                password);
    }

    protected void checkFileOwner(String userEmail, UUID fileId) throws ResponseStatusException {
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    protected void checkRestFileOwner(String userEmail, UUID fileId) throws ForbiddenFileAccessException {
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
    }

    protected void validateAuthentication(Authentication authentication) throws ResponseStatusException {
        if (authentication == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        if (!authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    protected String getUserEmailAddress(Authentication authentication) throws ResponseStatusException {
        if (authentication == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        if (!userDetails.isValid())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return userDetails.getEmailAddress();
    }

    protected String getRestUserEmailAddress(Authentication authentication) throws UnauthenticatedUserException {
        if (authentication == null)
            throw new UnauthenticatedUserException();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails == null)
            throw new UnauthenticatedUserException();
        if (!userDetails.isValid())
            throw new UnauthenticatedUserException();
        return userDetails.getEmailAddress();
    }

    protected UUID getUserId(Authentication authentication) throws ResponseStatusException {
        if (authentication == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        if (!userDetails.isValid())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return userDetails.getUserId();
    }

    protected String buildPath(String userName, String currentFolderPath, String fileName) throws NullPointerException {
        if (userName == null || currentFolderPath == null || fileName == null)
            throw new NullPointerException();
        String result = userName + " ://";
        if (!currentFolderPath.equals("")) {
            result = (result + currentFolderPath + "/");
        }
        if (!fileName.equals("")) {
            result = (result.substring(0, (result.length() - 1))  + fileName);
        }
        return result;
    }
}
