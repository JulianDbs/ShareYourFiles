package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.file.service.FileViewService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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
 * This class is used as an MVC controller and handles the '/desktop/file' GET AND POST requests.
 * @author JulianDbs
 */
@Controller
public class FileController {

    private final FileViewService fileViewService;

    public FileController(FileViewService fileViewService) {
        this.fileViewService = fileViewService;
    }

    @GetMapping("/desktop/file")
    public ModelAndView getFileView(Authentication authentication, ModelMap model) {
        return this.fileViewService.getFileView(authentication, model);
    }

    @PostMapping("/desktop/file")
    public ModelAndView redirectToFileView(Authentication authentication, ModelMap model, @RequestParam("fileId") UUID fileId) {
        return this.fileViewService.redirectToFileView(authentication, model, fileId);
    }
}
