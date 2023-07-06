package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.folder.dto.*;
import com.juliandbs.shareyourfiles.persistence.folder.service.FolderRestService;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.*;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;

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
 * This class is used as an REST controller and handles the '/folder/*' PUT, POST and PATH requests.
 * @author JulianDbs
 */
@RestController
@RequestMapping("/folder")
public class FolderRestController {

    private final FolderRestService folderRestService;

    public FolderRestController(FolderRestService folderRestService) {
        this.folderRestService = folderRestService;
    }

    @PutMapping("/add-folder")
    public ResponseEntity<Object> addNewFolder(Authentication authentication, @RequestBody NewFolderDto newFolderDto)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.addNewFolder(authentication, newFolderDto); }

    @PatchMapping("/show-folder")
    public ResponseEntity<Object> showFolder(Authentication authentication, @RequestBody FolderRequestDto folderRequest)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.changeFolderVisibility(authentication, folderRequest, true); }

    @PatchMapping("/hide-folder")
    public ResponseEntity<Object> hideFolder(Authentication authentication, @RequestBody FolderRequestDto folderRequest)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.changeFolderVisibility(authentication, folderRequest, false); }

    @PatchMapping("/set-password")
    public ResponseEntity<Object> setFolderPassword(final Authentication authentication, @RequestBody NewFolderPasswordDto newFolderPassword)
            throws UnauthenticatedUserException, FolderRequestException { return  this.folderRestService.setFolderPassword(authentication, newFolderPassword); }

    @PatchMapping("/change-password")
    public ResponseEntity<Object> changeFolderPassword(final Authentication authentication, final @RequestBody ChangeFolderPasswordDto changeFolderPassword)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.changeFolderPassword(authentication, changeFolderPassword); }

    @PatchMapping("/remove-password")
    public ResponseEntity<Object> removeFolderPassword(final Authentication authentication, final @RequestBody RemoveFolderPasswordDto removeFolderPassword)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.removeFolderPassword(authentication, removeFolderPassword); }

    @PatchMapping("/change-folder-name")
    public ResponseEntity<Object> changeFolderName(final Authentication authentication, final @RequestBody NewFolderNameDto newFolderName)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.changeFolderName(authentication, newFolderName); }

    @PatchMapping("/delete")
    public ResponseEntity<Object> deleteFolder(Authentication authentication, @RequestBody DeleteFolderDto deleteFolder)
            throws UnauthenticatedUserException, FolderRequestException { return this.folderRestService.deleteFolder(authentication, deleteFolder);
    }
}
