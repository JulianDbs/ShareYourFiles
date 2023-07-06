package com.juliandbs.shareyourfiles.persistence.folder.service;

import com.juliandbs.shareyourfiles.persistence.base.BaseService;
import com.juliandbs.shareyourfiles.persistence.file.dto.*;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.folder.dto.*;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderNotFoundException;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

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
 * This class extends the BaseService and is used as a service that manages the folder view requests.
 * @author JulianDbs
 */
@Service("folderViewService")
public class FolderViewService extends BaseService implements FolderViewServiceI {

    private final static String DESKTOP_VIEW_PATH = "desktop/desktop";
    private final static String DESKTOP_FOLDER_VIEW_PATH = "/desktop/folder";
    private final static String FOLDER_VIEW_PATH = "folder/folder";

    public FolderViewService(MessageSource messageSource, FolderRepository folderRepository, FileRepository fileRepository, FilePartsRepository filePartsRepository) {
        super(messageSource, folderRepository, fileRepository, filePartsRepository);
    }

    @Override
    public ModelAndView getRootView(Authentication authentication) throws ResponseStatusException {
        this.validateAuthentication(authentication);
        String emailAddress = this.getUserEmailAddress(authentication);
        UUID userId = this.getUserId(authentication);
        UUID rootFolderId;
        try {
             rootFolderId = this.folderRepository.getRootFolderId(userId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<FolderDto> rootFolderList = this.folderRepository.getRootFolderList(emailAddress, rootFolderId);
        boolean haveRootFolder = this.folderRepository.haveRootFolder(emailAddress, userId);
        if (!haveRootFolder)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        List<FileInfoDto> rootFileList = this.fileRepository.getRootFileList(emailAddress, rootFolderId);
        NewFileDto newFileDto = new NewFileDto("/desktop", rootFolderId);
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId);
        String path = this.buildPath(authentication.getName(), "", "");
        ModelAndView mav = new ModelAndView(DESKTOP_VIEW_PATH);
        mav.addObject("path", path);
        mav.addObject("user", authentication.getName());
        mav.addObject("email", emailAddress);
        mav.addObject("newFile", newFileDto);
        mav.addObject("folderList", rootFolderList);
        mav.addObject("folderRequest", new FolderRequestDto());
        mav.addObject("requestAction", DESKTOP_FOLDER_VIEW_PATH);
        mav.addObject("fileList", rootFileList);
        mav.addObject("newFolder", newFolderDto);
        return mav;
    }

    @Override
    public ModelAndView redirectToFolderView(Authentication authentication, ModelMap model, FolderRequestDto folderRequest) throws ResponseStatusException {
        if (authentication == null || model == null || folderRequest == null || !folderRequest.isValid())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.validateAuthentication(authentication);
        String emailAddress = this.getUserEmailAddress(authentication);
        UUID folderId = folderRequest.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean folderIsPrivate = this.folderRepository.folderIsPrivate(folderId);
        if (folderIsPrivate)
            this.checkFolderOwner(emailAddress, folderId);
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        if (folderHavePassword) {
            try {
                boolean folderMatchPassword = this.folderRepository.folderPasswordMatch(folderId, folderRequest.getFolderPassword());
                if (!folderMatchPassword) {
                    return this.buildDesktopWrongPasswordView(authentication, folderId);
                }
            } catch (FolderNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        return this.buildDesktopFolderView(authentication, folderId, emailAddress);
    }

    private ModelAndView buildDesktopFolderView(Authentication authentication, UUID folderId, String emailAddress) {
        UUID userId = this.getUserId(authentication);
        String path = this.buildFolderPath(authentication.getName(), userId, folderId);
        UUID parentFolderId = this.getParentFolderId(folderId);
        Boolean parentFolderIsRoot = this.checkParentFolder(userId, folderId);
        NewFolderDto newFolderDto = new NewFolderDto(folderId);
        List<FolderDto> rootFolderList = this.folderRepository.getRootFolderList(emailAddress, folderId);
        List<FileInfoDto> rootFileList = this.fileRepository.getRootFileList(emailAddress, folderId);
        boolean folderIsPrivate = this.folderRepository.folderIsPrivate(folderId);
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        boolean parentFolderHavePassword = this.folderRepository.folderHavePassword(parentFolderId);
        String folderName = this.getFolderName(folderId);
        NewFileDto newFileDto = new NewFileDto(DESKTOP_FOLDER_VIEW_PATH, folderId);
        ModelAndView mav = new ModelAndView(FOLDER_VIEW_PATH);
        mav.addObject("path", path);
        mav.addObject("parentFolderIsRoot", parentFolderIsRoot);
        mav.addObject("parentFolderId", parentFolderId);
        mav.addObject("parentFolderHavePassword", parentFolderHavePassword);
        mav.addObject("newFile", newFileDto);
        mav.addObject("folderList", rootFolderList);
        mav.addObject("folderRequest", new FolderRequestDto(folderId));
        mav.addObject("requestAction", "/desktop/folder");
        mav.addObject("fileList", rootFileList);
        mav.addObject("newFolder", newFolderDto);
        mav.addObject("folderIsPrivate", folderIsPrivate);
        mav.addObject("folderHavePassword", folderHavePassword);
        mav.addObject("folderId", folderId);
        mav.addObject("deleteFolderRequest", new DeleteFolderDto(folderId));
        mav.addObject("setPasswordRequest", new NewFolderPasswordDto(folderId));
        mav.addObject("changePasswordRequest", new ChangeFolderPasswordDto(folderId));
        mav.addObject("removePasswordRequest", new RemoveFolderPasswordDto(folderId));
        mav.addObject("folderName", folderName);
        mav.addObject("newFolderNameRequest", new NewFolderNameDto(folderId));
        return mav;
    }

    private ModelAndView buildDesktopWrongPasswordView(Authentication authentication, UUID folderId) {
        UUID userId = this.getUserId(authentication);
        String path = this.buildFolderPath(authentication.getName(), userId, folderId);
        UUID parentFolderId = this.getParentFolderId(folderId);
        String parentFolderName;
        try {
            parentFolderName = this.folderRepository.getFolderName(parentFolderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Boolean parentFolderIsRoot = this.checkParentFolder(userId, folderId);
        boolean folderIsPrivate = this.folderRepository.folderIsPrivate(folderId);
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        ModelAndView mav = new ModelAndView("desktop/desktop-wrong-password");
        mav.addObject("path", path);
        mav.addObject("parentFolderIsRoot", parentFolderIsRoot);
        mav.addObject("parentFolderId", parentFolderId);
        mav.addObject("folderRequest", new FolderRequestDto(parentFolderId));
        mav.addObject("folderIsPrivate", folderIsPrivate);
        mav.addObject("folderHavePassword", folderHavePassword);
        mav.addObject("requestAction", "/desktop/folder");
        mav.addObject("parentFolderName", parentFolderName);
        return mav;
    }

    private String getFolderName(UUID folderId) throws ResponseStatusException {
        String result;
        try {
            result = this.folderRepository.getFolderName(folderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private Boolean checkParentFolder(UUID userId, UUID folderId) throws ResponseStatusException {
        boolean result;
        try {
            UUID parentFolderId = this.folderRepository.getParentFolderId(folderId);
            UUID rootFolderId = this.folderRepository.getRootFolderId(userId);
            result = parentFolderId.equals(rootFolderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private UUID getParentFolderId(UUID folderId) throws ResponseStatusException {
        UUID result;
        try {
            result = this.folderRepository.getParentFolderId(folderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private void checkFolderOwner(String userEmail, UUID folderId) throws ResponseStatusException {
        boolean userIsOwner = this.folderRepository.userIsOwner(userEmail, folderId);
        if (!userIsOwner)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private String buildFolderPath(String userName, UUID userId, UUID folderId) throws ResponseStatusException {
        StringBuilder sb = new StringBuilder(userName + " ://");
        try {
            LinkedList<FolderPathDto> folderList = this.buildFolderPathList(userId, folderId);
            Collections.reverse(folderList);
            for (FolderPathDto folder : folderList) {
                sb.append(folder.getFolderName()).append("/");
            }
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return sb.toString();
    }

    private LinkedList<FolderPathDto> buildFolderPathList(UUID userId, UUID fileFolderId) throws FolderNotFoundException {
        LinkedList<FolderPathDto> result = new LinkedList<>();
        FolderPathDto fileFolderPathDto = this.folderRepository.getFolderPathInfo(fileFolderId);
        if (!fileFolderPathDto.getFolderName().equals("root")) {
            LinkedList<FolderPathDto> list = new LinkedList<>();
            list.add(fileFolderPathDto);
            result = this.buildFolderList(userId, list);
        }
        return result;
    }

    private LinkedList<FolderPathDto> buildFolderList(UUID userId, LinkedList<FolderPathDto> list) throws FolderNotFoundException {
        LinkedList<FolderPathDto> result = new LinkedList<>(list);
        for (int i = 0; i < 200; i++) {
            FolderPathDto folder = result.get(i);
            FolderPathDto parentFolder = this.folderRepository.getFolderPathInfo(folder.getFolderParentId());
            if (parentFolder.getFolderParentId().equals(userId))
                break;
            result.add(parentFolder);
        }
        return result;
    }

}
