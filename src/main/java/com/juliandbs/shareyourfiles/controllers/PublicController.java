package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderRequestDto;
import com.juliandbs.shareyourfiles.persistence.publicspace.service.PublicViewService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
 * This class is used as an MVC controller and handles the '/public/*' GET AND POST requests.
 * @author JulianDbs
 */
@Controller
public class PublicController {

    private final PublicViewService publicService;

    public PublicController(PublicViewService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/public")
    public ModelAndView getPublicView(Authentication authentication) {
        return this.publicService.getPublicView(authentication, 1);
    }

    @PostMapping("/public")
    public ModelAndView getPublicView(Authentication authentication, @RequestParam("offset") Integer offset) {
        return this.publicService.getPublicView(authentication, offset);
    }
    @GetMapping("/public/folder")
    public ModelAndView getPublicFolderView() {
        return new ModelAndView("redirect:/public");
    }

    @PostMapping("/public/folder")
    public ModelAndView redirectToPublicFolderView(Authentication authentication, @ModelAttribute FolderRequestDto folderRequest) {
        return this.publicService.redirectToPublicFolderView(authentication, folderRequest);
    }

    @PostMapping("/public/file")
    public ModelAndView redirectToPublicFileView(Authentication authentication, @RequestParam("fileId") UUID fileId) {
        return this.publicService.redirectToPublicFileView(authentication, fileId);
    }
}

