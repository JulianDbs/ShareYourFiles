package com.juliandbs.shareyourfiles.persistence.user.service;

import com.juliandbs.shareyourfiles.persistence.user.dto.*;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.*;
import com.juliandbs.shareyourfiles.persistence.user.service.exception.*;
import com.juliandbs.shareyourfiles.security.UserDetailsImpl;
import com.juliandbs.shareyourfiles.tools.RestResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
 * This class is used as a service that manages the user rest requests.
 * @author JulianDbs
 */
@Service("userRestService")
public class UserRestService implements UserRestServiceI {

    private static final int MAX_USER_USERNAME_LENGTH = 21;

    private static final int MIN_USER_USERNAME_LENGTH = 4;

    private static final int MAX_USER_PASSWORD_LENGTH = 50;

    private static final int MIN_USER_PASSWORD_LENGTH = 6;

    private final UserRepository userRepository;

    private final MessageSource messageSource;

    public UserRestService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    /**
     *  This method is used to process the '/user/change-username' PATCH rest endpoint requests and is used to change the current user username.
     * @param authentication  An Authentication interface instance that represents the request authenticated principal.
     * @param changeUsernameRequest A ChangeUsernameRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidChangeUsernameRequestException If the method arguments are null or the ChangeUsernameRequestDto instance is not valid.
     * @throws InvalidUserPasswordException If the user password is not the same as the current user password.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws NewUsernameIsTheSameException If the new username is the same as the original username.
     * @throws UserPasswordIsToShortException If the user password length is lower than the min length established by the MIN_USER_PASSWORD_LENGTH class property.
     * @throws UserPasswordIsToLongException If the user password length is greater than the max length established by the MAX_USER_PASSWORD_LENGTH class property.
     * @throws NewUsernameIsToShortException If the new username length is lower than the min length established by the MIN_USER_USERNAME_LENGTH class property.
     * @throws NewUsernameIsToLongException If the new username length is greater than the max length established by the MAX_USER_USERNAME_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> updateUsername(final Authentication authentication, final ChangeUsernameRequestDto changeUsernameRequest)
            throws InvalidChangeUsernameRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            UserDoesNotExistsException,
            NewUsernameIsTheSameException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            NewUsernameIsToShortException,
            NewUsernameIsToLongException {
        this.validateUpdateUsernameRequest(authentication, changeUsernameRequest);
        String emailAddress = this.getRestUserEmailAddress(authentication);
        try {
            this.userRepository.updateUsernameByEmail(emailAddress, changeUsernameRequest.getNewUsername());
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        } catch (SameUsernameException e) {
            throw new NewUsernameIsTheSameException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.change-account-username-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateUpdateUsernameRequest(final Authentication authentication, final ChangeUsernameRequestDto changeUsernameRequest)
            throws InvalidChangeUsernameRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            UserDoesNotExistsException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            NewUsernameIsToLongException,
            NewUsernameIsToShortException {
        if (authentication == null || changeUsernameRequest == null || !changeUsernameRequest.isValid())
            throw new InvalidChangeUsernameRequestException();
        if (!authentication.isAuthenticated())
            throw new UnauthenticatedUserException();
        String emailAddress = this.getRestUserEmailAddress(authentication);
        if (changeUsernameRequest.getNewUsername().length() > MAX_USER_USERNAME_LENGTH)
            throw new NewUsernameIsToLongException();
        if (changeUsernameRequest.getNewUsername().length() < MIN_USER_USERNAME_LENGTH)
            throw new NewUsernameIsToShortException();
        try {
            if (changeUsernameRequest.getPassword().length() > MAX_USER_PASSWORD_LENGTH)
                throw new UserPasswordIsToLongException();
            if (changeUsernameRequest.getPassword().length() < MIN_USER_PASSWORD_LENGTH)
                throw new UserPasswordIsToShortException();
            boolean passwordIsValid = this.userRepository.userPasswordMatch(emailAddress, changeUsernameRequest.getPassword());
            if (!passwordIsValid)
                throw new InvalidUserPasswordException();
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        }
    }

    /**
     *  This method is used to process the '/user/change-password' PATCH rest endpoint requests and is used to change the current user password.
     * @param authentication  An Authentication interface instance that represents the request authenticated principal.
     * @param changeAccountPasswordRequest A ChangeAccountPasswordRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidChangeAccountPasswordRequestException If the method arguments are null or the ChangeAccountPasswordRequestDto instance is not valid.
     * @throws InvalidUserPasswordException If the original user password is not the same as the current user password.
     * @throws PasswordsDoesNotMatchException If the new user password and the new user match password does not match.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws NewPasswordIsTheSameException If the new user password is the same as the original user password.
     * @throws UserNewPasswordIsToShortException If the new user password length is lower than the min length established by the MIN_USER_PASSWORD_LENGTH class property.
     * @throws UserNewPasswordIsToLongException If the new user password length is greater than the max length established by the MAX_USER_PASSWORD_LENGTH class property.
     * @throws UserPasswordIsToShortException If the original user password length is lower than the min length established by the MIN_USER_PASSWORD_LENGTH class property.
     * @throws UserPasswordIsToLongException If the original user password length is greater than the max length established by the MAX_USER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> updatePassword(final Authentication authentication, final ChangeAccountPasswordRequestDto changeAccountPasswordRequest)
            throws InvalidChangeAccountPasswordRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            PasswordsDoesNotMatchException,
            UserDoesNotExistsException,
            NewPasswordIsTheSameException,
            UserNewPasswordIsToShortException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            UserNewPasswordIsToLongException {
        this.validateChangePasswordRequest(authentication, changeAccountPasswordRequest);
        String emailAddress = this.getRestUserEmailAddress(authentication);
        try {
            this.userRepository.updatePasswordByEmail(emailAddress, changeAccountPasswordRequest.getNewPassword());
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        } catch (SamePasswordException e) {
            throw new NewPasswordIsTheSameException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.change-account-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateChangePasswordRequest(final Authentication authentication, final ChangeAccountPasswordRequestDto changeAccountPasswordRequest)
            throws InvalidChangeAccountPasswordRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            PasswordsDoesNotMatchException,
            UserDoesNotExistsException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            UserNewPasswordIsToLongException,
            UserNewPasswordIsToShortException {
        if (authentication == null || changeAccountPasswordRequest == null || !changeAccountPasswordRequest.isValid())
            throw new InvalidChangeAccountPasswordRequestException();
        if (!authentication.isAuthenticated())
            throw new UnauthenticatedUserException();
        String emailAddress = this.getRestUserEmailAddress(authentication);
        try {
            if (changeAccountPasswordRequest.getOriginalPassword().length() > MAX_USER_PASSWORD_LENGTH)
                throw new UserPasswordIsToLongException();
            if (changeAccountPasswordRequest.getOriginalPassword().length() < MIN_USER_PASSWORD_LENGTH)
                throw new UserPasswordIsToShortException();
            if (changeAccountPasswordRequest.getNewPassword().length() > MAX_USER_PASSWORD_LENGTH)
                throw new UserNewPasswordIsToLongException();
            if (changeAccountPasswordRequest.getNewPassword().length() < MIN_USER_PASSWORD_LENGTH)
                throw new UserNewPasswordIsToShortException();
            boolean passwordIsValid = this.userRepository.userPasswordMatch(emailAddress, changeAccountPasswordRequest.getOriginalPassword());
            if (!passwordIsValid)
                throw new InvalidUserPasswordException();
            if (!changeAccountPasswordRequest.getNewPassword().equals(changeAccountPasswordRequest.getNewMatchPassword()))
                throw new PasswordsDoesNotMatchException();
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        }
    }

    private String getRestUserEmailAddress(Authentication authentication) throws UnauthenticatedUserException {
        if (authentication == null)
            throw new UnauthenticatedUserException();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails == null)
            throw new UnauthenticatedUserException();
        if (!userDetails.isValid())
            throw new UnauthenticatedUserException();
        return userDetails.getEmailAddress();
    }


    /**
     *  This method is used to process the '/user/delete-account' PATCH rest endpoint requests and is used to delete the current user account.
     * @param authentication  An Authentication interface instance that represents the request authenticated principal.
     * @param deleteAccountRequest A DeleteAccountRequestDto that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidDeleteAccountRequestException  If the method arguments are null or the DeleteAccountRequestDto instance is not valid.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws PasswordsDoesNotMatchException If the user password and user match password does not match.
     * @throws InvalidUserPasswordException If the user password is not the same as the current user password.
     * @throws UserPasswordIsToShortException If the original user password length is lower than the min length established by the MIN_USER_PASSWORD_LENGTH class property.
     * @throws UserPasswordIsToLongException If the original user password length is greater than the max length established by the MAX_USER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> deleteUser(final Authentication authentication, final DeleteAccountRequestDto deleteAccountRequest)
            throws InvalidDeleteAccountRequestException,
            UnauthenticatedUserException,
            PasswordsDoesNotMatchException,
            InvalidUserPasswordException,
            UserDoesNotExistsException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException {
        this.validateDeleteUserRequest(authentication, deleteAccountRequest);
        String emailAddress = this.getRestUserEmailAddress(authentication);
        try {
            this.userRepository.deleteUserByEmail(emailAddress);
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.delete-account-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateDeleteUserRequest(final Authentication authentication, final DeleteAccountRequestDto deleteAccountRequest)
            throws InvalidDeleteAccountRequestException,
            UnauthenticatedUserException,
            PasswordsDoesNotMatchException,
            InvalidUserPasswordException,
            UserDoesNotExistsException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException {
        if (authentication == null || deleteAccountRequest == null || !deleteAccountRequest.isValid())
            throw new InvalidDeleteAccountRequestException();
        if (!authentication.isAuthenticated())
            throw new UnauthenticatedUserException();
        if (!deleteAccountRequest.getPassword().equals(deleteAccountRequest.getMatchPassword()))
            throw new PasswordsDoesNotMatchException();
        String emailAddress = this.getRestUserEmailAddress(authentication);
        if (deleteAccountRequest.getPassword().length() > MAX_USER_PASSWORD_LENGTH)
            throw new UserPasswordIsToLongException();
        if (deleteAccountRequest.getPassword().length() < MIN_USER_PASSWORD_LENGTH)
            throw new UserPasswordIsToShortException();
        try {
            boolean passwordIsValid = this.userRepository.userPasswordMatch(emailAddress, deleteAccountRequest.getPassword());
            if (!passwordIsValid)
                throw new InvalidUserPasswordException();
        } catch (UserNotFoundException e) {
            throw new UserDoesNotExistsException();
        }
    }
}