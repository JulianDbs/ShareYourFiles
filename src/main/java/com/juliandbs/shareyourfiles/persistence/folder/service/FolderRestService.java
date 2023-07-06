package com.juliandbs.shareyourfiles.persistence.folder.service;

import com.juliandbs.shareyourfiles.persistence.base.BaseService;
import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.folder.dto.*;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.FolderNotExistsException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderNotFoundException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.*;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.UnauthorizedUserException;
import com.juliandbs.shareyourfiles.tools.RestResponse;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.context.MessageSource;

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
 * This class extends the BaseService and is used as a service that manages the folder rest requests.
 * @author JulianDbs
 */
@Service("folderRestService")
public class FolderRestService extends BaseService implements FolderRestServiceI {

    private static final int MIN_FOLDER_NAME_LENGTH = 4;
    private static final int MAX_FOLDER_NAME_LENGTH = 40;
    private static final int MIN_FOLDER_PASSWORD_LENGTH = 6;
    private static final int MAX_FOLDER_PASSWORD_LENGTH = 50;

    public FolderRestService(MessageSource messageSource, FolderRepository folderRepository, FileRepository fileRepository, FilePartsRepository filePartsRepository) {
        super(messageSource, folderRepository, fileRepository, filePartsRepository);
    }

    /**
     * This method is used to process the '/folder/add-folder' PUT rest endpoint requests and add a new folder into the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param newFolderDto A NewFolderDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidAddNewFolderRequestException If the method arguments are null or the NewFolderDto instance is not valid.
     * @throws ParentFolderNotExistsException If folder in which you want to create the new folder does not exist.
     * @throws ForbiddenParentFolderAccessException If the current user is not the owner of the parent folder.
     * @throws FolderNameIsToShortException If the folder new name length is lower than the min length established by the MIN_FOLDER_NAME_LENGTH class property.
     * @throws FolderNameIsToLongException If the folder name new length is greater than the max length established by the MAX_FOLDER_NAME_LENGTH class property.
     * @throws FolderPasswordIsToShortException If the folder new password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the folder new password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordsDoesNotMatchException If the folder new password and the folder new match password are not equals.
     * @throws InterruptedFolderRequestException If the repository fails to add the new data into the database.
     */
    @Override
    public ResponseEntity<Object> addNewFolder(Authentication authentication, NewFolderDto newFolderDto)
            throws UnauthenticatedUserException,
            FolderNameIsToLongException,
            FolderPasswordsDoesNotMatchException,
            InvalidAddNewFolderRequestException,
            FolderNameIsToShortException,
            FolderPasswordIsToLongException,
            ForbiddenParentFolderAccessException,
            ParentFolderNotExistsException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException {
        this.validateAddNewFolderRequest(authentication, newFolderDto);
        UUID userId = this.getUserId(authentication);
        String emailAddress = this.getUserEmailAddress(authentication);
        return this.createNewFolder(emailAddress, userId, newFolderDto);
    }

    private void validateAddNewFolderRequest(final Authentication authentication, final NewFolderDto newFolderDto)
            throws UnauthenticatedUserException,
            InvalidAddNewFolderRequestException,
            ForbiddenParentFolderAccessException,
            FolderNameIsToLongException,
            FolderNameIsToShortException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            FolderPasswordsDoesNotMatchException,
            ParentFolderNotExistsException {
        if (authentication == null || newFolderDto == null ||!newFolderDto.isValid())
            throw new InvalidAddNewFolderRequestException();
        this.validateRestAuthentication(authentication);
        String emailAddress = this.getUserEmailAddress(authentication);
        UUID parentFolderId = newFolderDto.getFolderId();
        boolean parentFolderExists = this.folderRepository.folderExists(parentFolderId);
        if (!parentFolderExists)
            throw new ParentFolderNotExistsException();
        boolean userIsOwner = this.folderRepository.userIsOwner(emailAddress, parentFolderId);
        if (!userIsOwner)
            throw new ForbiddenParentFolderAccessException();
        if (newFolderDto.getFolderName().length() > MAX_FOLDER_NAME_LENGTH)
            throw new FolderNameIsToLongException();
        if (newFolderDto.getFolderName().length() < MIN_FOLDER_NAME_LENGTH)
            throw new FolderNameIsToShortException();
        if (newFolderDto.getFolderHavePassword()) {
            if (newFolderDto.getPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
                throw new FolderPasswordIsToLongException();
            if (newFolderDto.getPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
                throw new FolderPasswordIsToShortException();
            if (!newFolderDto.getPassword().equals(newFolderDto.getMatchPassword()))
                throw new FolderPasswordsDoesNotMatchException();
        }
    }

    private ResponseEntity<Object> createNewFolder(String userEmail, UUID userId, NewFolderDto newFolderDto) throws InvalidAddNewFolderRequestException, InterruptedFolderRequestException {
        UUID newFolderId;
        try {
            UUID rootFolderId = this.folderRepository.getRootFolderId(userId);
            boolean desktopFolder = newFolderDto.getFolderId().equals(rootFolderId);
            newFolderId = this.folderRepository.createNewFolder(userEmail, newFolderDto, desktopFolder);
        } catch (NullPointerException | FolderAlreadyExistsException | FolderNotFoundException e) {
            throw new InvalidAddNewFolderRequestException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.add-new-folder-success");
        return RestResponse.build(successMessage, HttpStatus.OK, newFolderId);
    }

    /**
     * This method is used to process the '/folder/show-folder' and '/folder/hide-folder' PATCH rest endpoint request and change the folder requested visibility.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param folderRequest A FolderRequestDto class instance that represents the request body data.
     * @param toPublic A boolean value that set if the folder visibility have to be changed to public or private.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidShowFolderRequestException If the method arguments are null or the FolderRequestDto instance is not valid.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderPasswordIsToShortException If the folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws InvalidFolderPasswordException If the folder password provided is invalid.
     * @throws InterruptedFolderRequestException If the repository fails to apply the update in the database table.
     */
    @Override
    public ResponseEntity<Object> changeFolderVisibility(final Authentication authentication, final FolderRequestDto folderRequest, final boolean toPublic)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidShowFolderRequestException,
            InterruptedFolderRequestException {
        this.validateFolderRequest(authentication, folderRequest);
        String userEmail = this.getUserEmailAddress(authentication);
        UUID folderId = folderRequest.getFolderId();
        try {
            if (toPublic) {
                this.folderRepository.showFolder(userEmail, folderId);
            } else {
                this.folderRepository.hideFolder(userEmail, folderId);
            }
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = (toPublic)? this.getLocalizedMessage("lang.message.show-folder-success") : this.getLocalizedMessage("lang.message.hide-folder-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateFolderRequest(final Authentication authentication, final FolderRequestDto folderRequest)
            throws UnauthenticatedUserException,
            InvalidShowFolderRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidFolderPasswordException {
        if (authentication == null || folderRequest == null || !folderRequest.isValid())
            throw new InvalidShowFolderRequestException();
        this.validateRestAuthentication(authentication);
        String userEmail = this.getUserEmailAddress(authentication);
        UUID folderId = folderRequest.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        boolean isOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!isOwner)
            throw new ForbiddenFolderAccessException();
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        if (folderHavePassword) {
            if (folderRequest.getFolderPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
                throw new FolderPasswordIsToLongException();
            if (folderRequest.getFolderPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
                throw new FolderPasswordIsToShortException();
            try {
                boolean validPassword = this.folderRepository.folderPasswordMatch(folderId, folderRequest.getFolderPassword());
                if (!validPassword)
                    throw new InvalidFolderPasswordException();
            } catch (FolderNotFoundException e) {
                throw new FolderNotExistsException();
            }
        }
    }

    /**
     * This method is used to process the '/folder/set-password' PATCH rest endpoint request and add a password to the requested folder.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param newFolderPassword A NewFolderPasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidFolderRequestException If the method arguments are null or the NewFolderPasswordDto instance is not valid.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderAlreadyHavePasswordException If the folder already have a password.
     * @throws FolderPasswordIsToShortException If the new folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the new folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordsDoesNotMatchException If the folder new password and the folder new match password are not equals.
     * @throws InterruptedFolderRequestException If the repository fails to apply the update in the database table.
     */
    @Override
    public ResponseEntity<Object> setFolderPassword(final Authentication authentication, final NewFolderPasswordDto newFolderPassword)
            throws UnauthenticatedUserException,
            FolderAlreadyHavePasswordException,
            FolderPasswordsDoesNotMatchException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException {
        this.validateSetFolderPasswordRequest(authentication, newFolderPassword);
        UUID folderId = newFolderPassword.getFolderId();
        String encodedPassword = this.passwordEncoder().encode(newFolderPassword.getPassword());
        try {
            this.folderRepository.setFolderPassword(folderId, encodedPassword);
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.set-folder-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateSetFolderPasswordRequest(final Authentication authentication, final NewFolderPasswordDto newFolderPassword)
            throws UnauthenticatedUserException,
            InvalidFolderRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderAlreadyHavePasswordException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            FolderPasswordsDoesNotMatchException {
        if (authentication == null || newFolderPassword == null || !newFolderPassword.isValid())
            throw new InvalidFolderRequestException();
        this.validateRestAuthentication(authentication);
        UUID folderId = newFolderPassword.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!userIsOwner)
            throw new ForbiddenFolderAccessException();
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        if (folderHavePassword)
            throw new FolderAlreadyHavePasswordException();
        if (newFolderPassword.getPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToLongException();
        if (newFolderPassword.getPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToShortException();
        if (!newFolderPassword.getPassword().equals(newFolderPassword.getMatchPassword()))
            throw new FolderPasswordsDoesNotMatchException();
    }

    /**
     * This method is used to process the '/folder/change-password' PATCH rest endpoint request and change the password of the requested folder.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param changeFolderPassword A NewFolderPasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidFolderRequestException If the method arguments are null or the ChangeFolderPasswordDto instance is not valid.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderPasswordIsToShortException If the new or original folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the new or original folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordsDoesNotMatchException If the new folder password and the new folder match password are not equals.
     * @throws InvalidFolderPasswordException If the original folder password provided is invalid.
     */
    @Override
    public ResponseEntity<Object> changeFolderPassword(final Authentication authentication, final ChangeFolderPasswordDto changeFolderPassword)
            throws UnauthenticatedUserException,
            FolderPasswordsDoesNotMatchException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException {
        this.validateChangeFolderPasswordRequest(authentication, changeFolderPassword);
        UUID folderId = changeFolderPassword.getFolderId();
        try {
            String encodedPassword = this.passwordEncoder().encode(changeFolderPassword.getNewPassword());
            this.folderRepository.changeFolderPassword(folderId, encodedPassword);
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.change-folder-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateChangeFolderPasswordRequest(final Authentication authentication, final ChangeFolderPasswordDto changeFolderPassword)
            throws UnauthenticatedUserException,
            InvalidFolderRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            FolderPasswordsDoesNotMatchException,
            InvalidFolderPasswordException {
        if (authentication == null || changeFolderPassword == null || !changeFolderPassword.isValid())
            throw new InvalidFolderRequestException();
        this.validateRestAuthentication(authentication);
        UUID folderId = changeFolderPassword.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!userIsOwner)
            throw new ForbiddenFolderAccessException();
        if (changeFolderPassword.getNewPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToLongException();
        if (changeFolderPassword.getNewPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToShortException();
        if (changeFolderPassword.getOriginalPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToLongException();
        if (changeFolderPassword.getOriginalPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToShortException();
        if (!changeFolderPassword.getNewPassword().equals(changeFolderPassword.getMatchNewPassword()))
            throw new FolderPasswordsDoesNotMatchException();
        try {
            boolean validOriginalPassword = this.folderRepository.folderPasswordMatch(folderId, changeFolderPassword.getOriginalPassword());
            if (!validOriginalPassword)
                throw new InvalidFolderPasswordException();
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        }
    }

    /**
     * This method is used to process the '/folder/remove-password' PATCH rest endpoint request and remove the password of the requested folder.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param removeFolderPassword A RemoveFolderPasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws InvalidFolderRequestException If the method arguments are null or the RemoveFolderPasswordDto instance is not valid.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderPasswordIsToShortException If the folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws InvalidFolderPasswordException If the folder password provided is invalid.
     */
    @Override
    public ResponseEntity<Object> removeFolderPassword(final Authentication authentication, final RemoveFolderPasswordDto removeFolderPassword)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            ForbiddenFolderAccessException,
            InvalidFolderRequestException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException {
        this.validateRemoveFolderPasswordRequest(authentication, removeFolderPassword);
        UUID folderId = removeFolderPassword.getFolderId();
        try {
            this.folderRepository.removeFolderPassword(folderId);
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.remove-folder-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateRemoveFolderPasswordRequest(final Authentication authentication, final RemoveFolderPasswordDto removeFolderPassword)
            throws UnauthenticatedUserException,
            InvalidFolderRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidFolderPasswordException {
        if (authentication == null || removeFolderPassword == null || !removeFolderPassword.isValid())
            throw new InvalidFolderRequestException();
        this.validateRestAuthentication(authentication);
        UUID folderId = removeFolderPassword.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!userIsOwner)
            throw new ForbiddenFolderAccessException();
        if (removeFolderPassword.getPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToLongException();
        if (removeFolderPassword.getPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
            throw new FolderPasswordIsToShortException();
        try {
            boolean validOriginalPassword = this.folderRepository.folderPasswordMatch(folderId, removeFolderPassword.getPassword());
            if (!validOriginalPassword)
                throw new InvalidFolderPasswordException();
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        }
    }

    /**
     * This method is used to process the '/folder/change-name' PATCH rest endpoint request and change the name of the requested folder.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param newFolderName A NewFolderNameDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws InvalidUpdateFolderNameRequestException If the method arguments are null or the NewFolderNameDto instance is not valid.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderNameIsToShortException If the folder new name length is lower than the min length established by the MIN_FOLDER_NAME_LENGTH class property.
     * @throws FolderNameIsToLongException If the folder new name length is greater than the max length established by the MAX_FOLDER_NAME_LENGTH class property.
     * @throws SameFolderNameException If the folder new name is the same as the original folder name.
     * @throws FolderPasswordIsToShortException If the folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws InvalidFolderPasswordException If the folder password provided is invalid.
     */
    @Override
    public ResponseEntity<Object> changeFolderName(final Authentication authentication, final NewFolderNameDto newFolderName)
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
            InterruptedFolderRequestException {
        this.validateChangeFolderNameRequest(authentication, newFolderName);
        UUID folderId = newFolderName.getFolderId();
        try {
            this.folderRepository.changeFolderName(folderId, newFolderName.getNewName());
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.update-folder-name-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateChangeFolderNameRequest(final Authentication authentication, final NewFolderNameDto newFolderName)
            throws UnauthenticatedUserException,
            InvalidUpdateFolderNameRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderNameIsToLongException,
            FolderNameIsToShortException,
            SameFolderNameException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidFolderPasswordException {
        if (authentication == null || newFolderName == null || !newFolderName.isValid())
            throw new InvalidUpdateFolderNameRequestException();
        this.validateRestAuthentication(authentication);
        String userEmail = this.getUserEmailAddress(authentication);
        UUID folderId = newFolderName.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        boolean isOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!isOwner)
            throw new ForbiddenFolderAccessException();
        if (newFolderName.getNewName().length() > MAX_FOLDER_NAME_LENGTH)
            throw new FolderNameIsToLongException();
        if (newFolderName.getNewName().length() < MIN_FOLDER_NAME_LENGTH)
            throw new FolderNameIsToShortException();
        try {
            String originalFolderName = this.folderRepository.getFolderName(folderId);
            if (newFolderName.getNewName().equals(originalFolderName))
                throw new SameFolderNameException();
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        }
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        try {
            if (folderHavePassword) {
                if (newFolderName.getPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
                    throw new FolderPasswordIsToLongException();
                if (newFolderName.getPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
                    throw new FolderPasswordIsToShortException();
                boolean validOriginalPassword = this.folderRepository.folderPasswordMatch(folderId, newFolderName.getPassword());
                if (!validOriginalPassword)
                    throw new InvalidFolderPasswordException();
            }
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        }
    }

    /**
     * This method is used to process the '/folder/delete' PATCH rest endpoint request and delete the requested folder.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param deleteFolder A DeleteFolderDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FolderNotExistsException If the folder requested does not exist.
     * @throws InvalidDeleteFolderRequestException If the method arguments are null or the DeleteFolderDto instance is not valid.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the folder.
     * @throws FolderPasswordIsToShortException If the folder password length is lower than the min length established by the MIN_FOLDER_PASSWORD_LENGTH class property.
     * @throws FolderPasswordIsToLongException If the folder password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws InvalidFolderPasswordException If the folder password provided is invalid.
     */
    @Override
    public ResponseEntity<Object> deleteFolder(final Authentication authentication, final DeleteFolderDto deleteFolder)
            throws UnauthenticatedUserException,
            FolderNotExistsException,
            InvalidFolderPasswordException,
            InvalidDeleteFolderRequestException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InterruptedFolderRequestException {
        this.validateRemoveFolderRequest(authentication, deleteFolder);
        String userEmail = this.getUserEmailAddress(authentication);
        UUID folderId = deleteFolder.getFolderId();
        try {
            this.folderRepository.deleteFolder(userEmail, folderId);
        } catch (FolderNotFoundException e) {
            throw new FolderNotExistsException();
        } catch (UnauthorizedUserException e) {
            throw new ForbiddenFolderAccessException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFolderRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.delete-folder-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateRemoveFolderRequest(final Authentication authentication, final DeleteFolderDto deleteFolder)
            throws UnauthenticatedUserException,
            InvalidDeleteFolderRequestException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FolderPasswordIsToLongException,
            FolderPasswordIsToShortException,
            InvalidFolderPasswordException {
        if (authentication == null || deleteFolder == null || !deleteFolder.isValid())
            throw new InvalidDeleteFolderRequestException();
        this.validateRestAuthentication(authentication);
        String userEmail = this.getUserEmailAddress(authentication);
        UUID folderId = deleteFolder.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        boolean isOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!isOwner)
            throw new ForbiddenFolderAccessException();
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        if (folderHavePassword) {
            try {
                if (deleteFolder.getPassword().length() > MAX_FOLDER_PASSWORD_LENGTH)
                    throw new FolderPasswordIsToLongException();
                if (deleteFolder.getPassword().length() < MIN_FOLDER_PASSWORD_LENGTH)
                    throw new FolderPasswordIsToShortException();
                boolean validFolderPassword = this.folderRepository.folderPasswordMatch(folderId, deleteFolder.getPassword());
                if (!validFolderPassword)
                    throw new InvalidFolderPasswordException();
            } catch (FolderNotFoundException e) {
                throw new FolderNotExistsException();
            }
        }
    }

}
