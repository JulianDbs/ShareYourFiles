package com.juliandbs.shareyourfiles.persistence.publicspace.service;

import com.juliandbs.shareyourfiles.persistence.base.BaseService;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileInfoDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileRequestDto;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderRequestDto;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderNotFoundException;
import com.juliandbs.shareyourfiles.persistence.file.repository.exception.FileNotFoundException;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;
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
 * This class extends the BaseService and is used as a service that manages the public view requests.
 * @author JulianDbs
 */
@Service("publicService")
public class PublicViewService extends BaseService implements PublicViewServiceI {

    private static final String PUBLIC_VIEW_PATH = "public/public";

    private static final String PUBLIC_FOLDER_VIEW_PATH = "folder/public-folder";

    private static final String PUBLIC_WRONG_PASSWORD_VIEW_PATH = "public/public-wrong-password";

    private static final String PUBLIC_FILE_VIEW_PATH = "file/public-file";

    public PublicViewService(MessageSource messageSource, FolderRepository folderRepository, FileRepository fileRepository, FilePartsRepository filePartsRepository) {
        super(messageSource, folderRepository, fileRepository, filePartsRepository);
    }

    @Override
    public ModelAndView getPublicView(Authentication authentication, Integer offset) throws ResponseStatusException {
        this.validateAuthentication(authentication);
        return this.buildPublicView(offset);
    }

    private ModelAndView buildPublicView(int offset) {
        int value = (offset > 1)? ((offset - 1) * 20) : 0;
        LinkedList<FolderDto> rootFolderList = this.folderRepository.findPublicFolderListWithOffset(value);
        ModelAndView mav = new ModelAndView(PUBLIC_VIEW_PATH);
        mav.addObject("folderList", rootFolderList);
        mav.addObject("folderRequest", new FolderRequestDto());
        mav.addObject("requestAction", "/public/folder");
        return this.setPaginationBar(offset, mav);
    }

    private ModelAndView setPaginationBar(int offset, ModelAndView mav) {
        long publicFolderCount = this.folderRepository.publicFolderCount();
        long p = (publicFolderCount / 20);
        long pages = (publicFolderCount > (20 * p))? (p + 1) : p;
        List<Integer> buttonList = new LinkedList<>();
        for (int i = 0; i < pages; i++) {buttonList.add( (i + 1) );}
        int leftOffset = (offset - 1);
        leftOffset = (leftOffset < 1)? offset: leftOffset;
        int rightOffset = (offset + 1);
        rightOffset = (rightOffset > pages)? offset: rightOffset;
        mav.addObject("offsetList", buttonList);
        mav.addObject("currentOffset", offset);
        mav.addObject("leftOffset", leftOffset);
        mav.addObject("rightOffset", rightOffset);
        return mav;
    }

    @Override
    public ModelAndView redirectToPublicFolderView(Authentication authentication, FolderRequestDto folderRequest) throws ResponseStatusException {
        if (authentication == null || folderRequest == null || !folderRequest.isValid())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.validateAuthentication(authentication);
        UUID folderId = folderRequest.getFolderId();
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean folderHavePassword = this.folderRepository.folderHavePassword(folderId);
        if (folderHavePassword) {
            try {
                boolean folderMatchPassword = this.folderRepository.publicFolderPasswordMatch(folderId, folderRequest.getFolderPassword());
                if (!folderMatchPassword) {
                    return this.buildPublicFolderWrongPassword();
                }
            } catch (FolderNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        String ownerUsername;
        String folderName;
        try {
            ownerUsername = this.folderRepository.getOwnerUsernameByFolderId(folderId);
            folderName = this.folderRepository.getFolderName(folderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return this.buildPublicFolderView(folderId, ownerUsername, folderName);
    }

    private ModelAndView buildPublicFolderView(UUID folderId, String ownerUsername, String folderName) {
        List<FolderDto> rootFolderList = this.folderRepository.findPublicSubFoldersByFolderId(folderId);
        List<FileInfoDto> rootFileList = this.fileRepository.findPublicFilesByFolderId(folderId);
        ModelAndView mav = new ModelAndView(PUBLIC_FOLDER_VIEW_PATH);
        mav.addObject("folderList", rootFolderList);
        mav.addObject("fileList", rootFileList);
        mav.addObject("folderRequest", new FolderRequestDto());
        mav.addObject("requestAction", "/public/folder");
        mav.addObject("ownerUsername", ownerUsername);
        mav.addObject("folderName", folderName);
        return mav;
    }

    private ModelAndView buildPublicFolderWrongPassword() {
        return new ModelAndView(PUBLIC_WRONG_PASSWORD_VIEW_PATH);
    }

    @Override
    public ModelAndView redirectToPublicFileView(Authentication authentication, UUID fileId) throws ResponseStatusException {
        if (authentication == null || fileId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        this.validateAuthentication(authentication);
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean fileIsPublic = this.fileRepository.fileIsPublic(fileId);
        if (!fileIsPublic)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String fileOriginalName;
        FileInfoDto fileInfo;
        UUID folderId;
        try {
            fileInfo = this.fileRepository.getFileInfo(fileId);
            fileOriginalName = fileInfo.getFileOriginalName();
            folderId = this.fileRepository.getFileFolderId(fileId);
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        String ownerUsername;
        try {
            ownerUsername = this.folderRepository.getOwnerUsernameByFolderId(folderId);
        } catch (FolderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ModelAndView mav = new ModelAndView(PUBLIC_FILE_VIEW_PATH);
        mav.addObject("fileInfo", fileInfo);
        mav.addObject("fileRequest", new FileRequestDto(fileId, "", fileOriginalName));
        mav.addObject("folderId", folderId);
        mav.addObject("ownerUsername", ownerUsername);
        return mav;
    }
}
