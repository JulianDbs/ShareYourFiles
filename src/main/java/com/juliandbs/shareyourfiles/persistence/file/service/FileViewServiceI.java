package com.juliandbs.shareyourfiles.persistence.file.service;

import com.juliandbs.shareyourfiles.persistence.file.dto.FileRequestDto;

import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ResponseStatusException;
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
 * This interface set the base methods of the FileViewService class.
 * @author JulianDbs
 */
public interface FileViewServiceI {

    ModelAndView getFileView(Authentication authentication, ModelMap model);

    ModelAndView redirectToFileView(Authentication authentication, ModelMap model, UUID fileId) throws ResponseStatusException;

}
