package com.juliandbs.shareyourfiles.persistence.file.service;

import com.juliandbs.shareyourfiles.persistence.base.BaseService;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderPathDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderRequestDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.*;
import com.juliandbs.shareyourfiles.persistence.file.repository.exception.FileNotFoundException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderNotFoundException;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

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
 * This class extends the BaseService and is used as a service that manages the file view requests.
 * @author JulianDbs
 */
@Service("fileViewService")
public class FileViewService extends BaseService implements FileViewServiceI {

    private static final String FILE_VIEW_PATH = "file/file";

    public FileViewService(MessageSource messageSource, FolderRepository folderRepository, FileRepository fileRepository, FilePartsRepository filePartsRepository) {
        super(messageSource, folderRepository, fileRepository, filePartsRepository);
    }

    @Override
    public ModelAndView getFileView(Authentication authentication, ModelMap model) {
        this.validateAuthentication(authentication);
        return new ModelAndView(FILE_VIEW_PATH, model);
    }

    @Override
    public ModelAndView redirectToFileView(Authentication authentication, ModelMap model, UUID fileId) {
        if (authentication == null || model == null || fileId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.validateAuthentication(authentication);
        String emailAddress = this.getUserEmailAddress(authentication);
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean fileIsPublic = this.fileRepository.fileIsPublic(fileId);
        if (!fileIsPublic)
            this.checkFileOwner(emailAddress, fileId);
        UUID userId = this.getUserId(authentication);
        String fileOriginalName;
        FileInfoDto fileInfo;
        UUID folderId;
        String parentFolderName;
        try {
            fileInfo = this.fileRepository.getFileInfo(fileId);
            fileOriginalName = fileInfo.getFileOriginalName();
            folderId = this.fileRepository.getFileFolderId(fileId);
            parentFolderName = this.folderRepository.getFolderName(folderId);
        } catch (FileNotFoundException | FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boolean folderParentHavePassword = this.folderRepository.folderHavePassword(folderId);
        ModelAndView mav = new ModelAndView(FILE_VIEW_PATH);
        mav.addObject("fileInfo", fileInfo);
        mav.addObject("fileRequest", new FileRequestDto(fileId, "", fileOriginalName));
        String fileFolderPath = this.buildFilePath(userId, fileId);
        String path = this.buildPath(authentication.getName(), fileFolderPath, fileInfo.getFileName());
        mav.addObject("path", path);
        mav.addObject("folderId", folderId);
        mav.addObject("setPasswordRequest", new NewFilePasswordDto(fileId));
        mav.addObject("changePasswordRequest", new ChangeFilePasswordDto(fileId));
        mav.addObject("removePasswordRequest", new RemoveFilePasswordDto(fileId));
        mav.addObject("parentFolderName", parentFolderName);
        mav.addObject("folderRequest", new FolderRequestDto(folderId));
        mav.addObject("requestAction", "/desktop/folder");
        mav.addObject("folderParentHavePassword", folderParentHavePassword);
        return mav;
    }

    private String buildFilePath(UUID userId, UUID fileId) throws ResponseStatusException {
        StringBuilder sb = new StringBuilder();
        try {
            UUID fileFolderId = this.fileRepository.getFileFolderId(fileId);
            List<FolderPathDto> folderList = this.buildFolderPathList(userId, fileFolderId);
            for (FolderPathDto folder : folderList) {
                sb.append(folder.getFolderName()).append("/");
            }
        } catch (FileNotFoundException | FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return sb.toString();
    }

    private List<FolderPathDto> buildFolderPathList(UUID userId, UUID fileFolderId) throws FolderNotFoundException {
        List<FolderPathDto> result = new LinkedList<>();
        FolderPathDto fileFolderPathDto = this.folderRepository.getFolderPathInfo(fileFolderId);
        if (!fileFolderPathDto.getFolderName().equals("root")) {
            LinkedList<FolderPathDto> list = new LinkedList<>();
            list.add(fileFolderPathDto);
            result = this.buildFolderList(userId, list);
        }
        return result;
    }

    private List<FolderPathDto> buildFolderList(UUID userId, LinkedList<FolderPathDto> list) throws FolderNotFoundException {
        List<FolderPathDto> result = new LinkedList<>(list);
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
