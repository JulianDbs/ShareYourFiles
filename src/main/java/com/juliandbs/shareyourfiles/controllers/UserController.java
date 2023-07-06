package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.persistence.user.dto.UserDto;
import com.juliandbs.shareyourfiles.persistence.user.service.UserViewService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

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
 * This class is used as an MVC controller and handles the accounts related GET AND POST requests.
 * @author JulianDbs
 */
@Controller
public class UserController {

    private final UserViewService userViewService;

    public UserController(UserViewService userViewService) {
        this.userViewService = userViewService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginView(Authentication authentication) {
        return this.userViewService.getLoginView(authentication);
    }

    @GetMapping("/registration")
    public ModelAndView getRegistrationView(Authentication authentication) {
        return this.userViewService.getRegistrationView(authentication);
    }

    @PostMapping("/registration")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserDto user, Errors errors, Model model ) {
        return this.userViewService.registerUser(user, errors, model);
    }
    @GetMapping("/account")
    public ModelAndView getProfileView(final Authentication authentication) {
        return this.userViewService.getAccountView(authentication);
    }
}
