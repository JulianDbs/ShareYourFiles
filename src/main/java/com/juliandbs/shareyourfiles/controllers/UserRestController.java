package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.user.dto.ChangeAccountPasswordRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.ChangeUsernameRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.DeleteAccountRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.*;
import com.juliandbs.shareyourfiles.persistence.user.service.UserRestService;
import com.juliandbs.shareyourfiles.persistence.user.service.exception.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
 * This class is used as an REST controller and handles the '/user/*' PATCH requests.
 * @author JulianDbs
 */
@RestController
@RequestMapping("/user")
public class UserRestController {

    private final UserRestService userRestService;

    public UserRestController(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    @PatchMapping("/change-username")
    public ResponseEntity<Object> updateUsername(final Authentication authentication, final @RequestBody ChangeUsernameRequestDto changeUsernameRequest)
            throws UnauthenticatedUserException, UserRestException { return userRestService.updateUsername(authentication, changeUsernameRequest); }

    @PatchMapping("/change-password")
    public ResponseEntity<Object> updatePassword(final Authentication authentication, final @RequestBody ChangeAccountPasswordRequestDto changeAccountPasswordRequest)
            throws UnauthenticatedUserException, UserRestException { return userRestService.updatePassword(authentication, changeAccountPasswordRequest); }

    @PatchMapping("/delete-account")
    public ResponseEntity<Object> deleteAccount(final Authentication authentication, final @RequestBody DeleteAccountRequestDto deleteAccountRequest)
            throws UnauthenticatedUserException, UserRestException { return userRestService.deleteUser(authentication, deleteAccountRequest); }
}
