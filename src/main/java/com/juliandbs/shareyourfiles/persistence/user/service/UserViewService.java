package com.juliandbs.shareyourfiles.persistence.user.service;

import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;
import com.juliandbs.shareyourfiles.persistence.user.dto.*;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.*;
import com.juliandbs.shareyourfiles.security.UserDetailsImpl;
import com.juliandbs.shareyourfiles.validation.filters.ValidationErrorFilter;
import com.juliandbs.shareyourfiles.services.response.CustomResponse;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserInfoRepository;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserInfoEntity;
import com.juliandbs.shareyourfiles.tools.PropertyImporter;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.LinkedList;
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
 * This class extends the BaseService and is used as a service that manages the user view requests.
 * @author JulianDbs
 */
@Service("userViewService")
public class UserViewService implements UserViewServiceI, EnvironmentAware {

    private static final String REGISTRATION_VIEW_PATH = "registration/registration";

    private static final String REGISTRATION_SUCCESS_VIEW_PATH = "registration/registration_success";

    private static final String LOGIN_VIEW_PATH = "login/login";

    private static final String ACCOUNT_VIEW_PATH = "account/account";

    private final ValidationErrorFilter validationErrorFilter;

    private final UserRepository userRepository;

    private final FolderRepository folderRepository;

    private final UserInfoRepository userInfoRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private Boolean canCreateNewAccount;

    public UserViewService(ValidationErrorFilter validationErrorFilter, UserRepository userRepository, FolderRepository folderRepository, UserInfoRepository userInfoRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.validationErrorFilter = validationErrorFilter;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.userInfoRepository = userInfoRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void setEnvironment(@NonNull final Environment environment) {
        this.canCreateNewAccount = PropertyImporter.getCanCreateAccountProperty(environment);
    }

    @Override
    public CustomResponse createNewUser(Model model, Errors errors, UserDto newUser) {
        String toUrl = "redirect:/login";
        if (!canCreateNewAccount) {
            return new CustomResponse(toUrl, model);
        }
        if (errors.hasErrors()) {
            toUrl = REGISTRATION_VIEW_PATH;
            model.addAttribute("usernameErrors", validationErrorFilter.getUsernameErrors(errors));
            model.addAttribute("emailErrors", validationErrorFilter.getEmailErrors(errors));
            model.addAttribute("passwordErrors", validationErrorFilter.getPasswordErrors(errors));
            model.addAttribute("matchingPasswordErrors", validationErrorFilter.getMatchingPasswordErrors(errors));
        } else {
            UserEntity user = new UserEntity(
                    newUser.getUsername(),
                    newUser.getEmail(),
                    bCryptPasswordEncoder.encode(newUser.getPassword())
            );
            UserInfoEntity userInfo = new UserInfoEntity(newUser.getEmail());
            try {
                UUID userId = this.userRepository.addNewUser(user);
                this.userInfoRepository.addNewUserInfo(userInfo);
                UUID folderId = this.folderRepository.createRootFolder(user.getEmail(), userId);
                model.addAttribute("name", user.getUsername());
                toUrl = REGISTRATION_SUCCESS_VIEW_PATH;
            } catch (UserAlreadyExistsException | UserInfoAlreadyExistsException e) {
                List<String> errorList = new LinkedList<>();
                errorList.add("User Already Exists");
                model.addAttribute("emailErrors", errorList);
                toUrl = REGISTRATION_VIEW_PATH;
            } catch (UnfinishedRepositoryOperationException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new CustomResponse(toUrl, model);
    }

    @Override
    public ModelAndView getLoginView(Authentication authentication) {
        String view = LOGIN_VIEW_PATH;
        if (authentication != null) {
            view = "redirect:/desktop";
        }
        return new ModelAndView(view);
    }

    @Override
    public ModelAndView getRegistrationView(Authentication authentication) {
        ModelAndView mav;
        if (authentication == null) {
            mav = new ModelAndView("redirect:/login");
            if (canCreateNewAccount) {
                mav = new ModelAndView(REGISTRATION_VIEW_PATH);
                mav.addObject("user", new RegistrationFormDto());
            }
        } else {
            mav = new ModelAndView("redirect:/desktop");
        }
        return mav;
    }

    public String registerUser(UserDto user, Errors errors, Model model ) {
        CustomResponse customResponse = this.createNewUser(model, errors, user);
        return customResponse.getUrl();
    }

    @Override
    public ModelAndView getAccountView(final Authentication authentication) {
        ModelAndView mav = new ModelAndView(ACCOUNT_VIEW_PATH);
        AccountInfoDto accountInfoDto;
        try {
            accountInfoDto = this.userRepository.getAccountInfo(this.getUserEmailFomAuthentication(authentication));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        mav.addObject("accountInfo", accountInfoDto);
        mav.addObject("changeUsernameRequest", new ChangeUsernameRequestDto());
        mav.addObject("changePasswordRequest", new ChangeAccountPasswordRequestDto());
        mav.addObject("deleteAccountRequest", new DeleteAccountRequestDto());
        return mav;
    }

    private String getUserEmailFomAuthentication(final Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getEmailAddress();
    }

}