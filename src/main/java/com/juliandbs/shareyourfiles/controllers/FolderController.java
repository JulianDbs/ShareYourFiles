package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderRequestDto;
import com.juliandbs.shareyourfiles.persistence.folder.service.FolderViewService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
 * This class is used as an MVC controller and handles the '/desktop/folder' GET AND POST request.
 * @author JulianDbs
 */
@Controller
public class FolderController {

    private final FolderViewService folderViewService;

    public FolderController(FolderViewService folderViewService) {
        this.folderViewService = folderViewService;
    }

    @GetMapping("/desktop")
    public ModelAndView getDesktopView(Authentication authentication) {
        return this.folderViewService.getRootView(authentication);
    }

    @GetMapping("/desktop/folder")
    public ModelAndView getFolderView() {
        return new ModelAndView("redirect:/desktop");
    }

    @PostMapping("/desktop/folder")
    public ModelAndView redirectToFolderView(Authentication authentication, ModelMap model, @ModelAttribute FolderRequestDto folderRequest) {
        return this.folderViewService.redirectToFolderView(authentication, model, folderRequest);
    }

}
