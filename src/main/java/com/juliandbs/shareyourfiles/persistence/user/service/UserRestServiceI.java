package com.juliandbs.shareyourfiles.persistence.user.service;

import com.juliandbs.shareyourfiles.persistence.user.dto.*;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;
import com.juliandbs.shareyourfiles.persistence.user.service.exception.*;

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
 * This interface set the base methods of the UserRestService class.
 * @author JulianDbs
 */
public interface UserRestServiceI {

    ResponseEntity<Object> updateUsername(final Authentication authentication,final ChangeUsernameRequestDto changeUsernameRequest)
            throws InvalidChangeUsernameRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            UserDoesNotExistsException,
            NewUsernameIsTheSameException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            NewUsernameIsToShortException,
            NewUsernameIsToLongException;

    ResponseEntity<Object> updatePassword(final Authentication authentication, final ChangeAccountPasswordRequestDto changeAccountPasswordRequest)
            throws InvalidChangeAccountPasswordRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            PasswordsDoesNotMatchException,
            UserDoesNotExistsException,
            UserNewPasswordIsToShortException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException,
            UserNewPasswordIsToLongException,
            NewPasswordIsTheSameException;

    ResponseEntity<Object> deleteUser(final Authentication authentication, final DeleteAccountRequestDto deleteAccountRequest)
            throws InvalidDeleteAccountRequestException,
            UnauthenticatedUserException,
            InvalidUserPasswordException,
            PasswordsDoesNotMatchException,
            UserDoesNotExistsException,
            UserPasswordIsToLongException,
            UserPasswordIsToShortException;

}