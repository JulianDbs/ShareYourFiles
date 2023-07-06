package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.file.dto.*;
import com.juliandbs.shareyourfiles.persistence.file.service.FileRestService;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.*;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.FolderRequestException;
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
 * This class is used as an REST controller and handles the '/file/*' PUT, POST and PATH requests.
 * @author JulianDbs
 */

@RestController
@RequestMapping("/file")
public class FileRestController {

    private final FileRestService fileRestService;

    public FileRestController(FileRestService fileRestService) {
        this.fileRestService = fileRestService;
    }

    @PutMapping("/add-file")
    public ResponseEntity<Object> addNewFile(Authentication authentication, @ModelAttribute NewFileDto newFile)
            throws UnauthenticatedUserException,
            FileRequestException,
            FolderRequestException { return this.fileRestService.storeNewFile(authentication, newFile);  }

    @PostMapping("/get-file")
    public ResponseEntity<byte[]> getFileResource(Authentication authentication, @ModelAttribute FileRequestDto fileRequest)
            throws UnauthenticatedUserException, FileRequestException { return this.fileRestService.getFileResource(authentication, fileRequest); }

    @PatchMapping("/show-file")
    public ResponseEntity<Object> showFile(final Authentication authentication, final @RequestBody FileRequestDto fileRequest)
            throws UnauthenticatedUserException, FileRequestException { return this.fileRestService.showFile(authentication, fileRequest); }

    @PatchMapping("/hide-file")
    public ResponseEntity<Object> hideFile(final Authentication authentication, final @RequestBody FileRequestDto fileRequest)
            throws UnauthenticatedUserException,FileRequestException { return  this.fileRestService.hideFile(authentication, fileRequest); }

    @PatchMapping("/set-password")
    public ResponseEntity<Object> setFilePassword(final Authentication authentication, @RequestBody NewFilePasswordDto newFilePassword)
            throws UnauthenticatedUserException, FileRequestException { return  this.fileRestService.setFilePassword(authentication, newFilePassword); }

    @PatchMapping("/change-password")
    public ResponseEntity<Object> changeFilePassword(final Authentication authentication, final @RequestBody ChangeFilePasswordDto changeFilePassword)
            throws UnauthenticatedUserException, FileRequestException { return this.fileRestService.changeFilePassword(authentication, changeFilePassword); }

    @PatchMapping("/remove-password")
    public ResponseEntity<Object> removeFilePassword(final Authentication authentication, final @RequestBody RemoveFilePasswordDto removeFilePassword)
            throws UnauthenticatedUserException, FileRequestException { return this.fileRestService.removeFilePassword(authentication, removeFilePassword); }

    @PatchMapping("/delete")
    public ResponseEntity<Object> deleteFile(final Authentication authentication, final @RequestBody FileRequestDto fileRequest)
            throws UnauthenticatedUserException, FileRequestException { return this.fileRestService.deleteFile(authentication, fileRequest); }
}
