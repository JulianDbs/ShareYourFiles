package com.juliandbs.shareyourfiles.persistence.folder.repository;

import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderPathDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.entity.FolderEntity;
import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderNotFoundException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.UnauthorizedUserException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
 * This class is used as a repository to manage the content of the 'folders' table.
 * @author JulianDbs
 */
@Repository
public interface FolderRepository extends org.springframework.data.repository.Repository<FolderEntity, Long> , FolderRepositoryExtraOperations {

    default boolean publicFolderPasswordMatch(final UUID fileId, final String rawFilePassword) throws NullPointerException, FolderNotFoundException {
        if (fileId == null || rawFilePassword == null)
            throw new NullPointerException();
        Optional<String> result = this.findPublicFolderPassword(fileId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(rawFilePassword, result.get());
    }

    default UUID createRootFolder(String userEmail, UUID userId) throws NullPointerException, UnfinishedRepositoryOperationException {
        if (userEmail == null || userId == null)
            throw new NullPointerException();
        boolean exists = this.haveRootFolder(userEmail, userId);
        if (exists)
            throw new UnfinishedRepositoryOperationException();
        return this.addRootFolder(userEmail, userId);
    }

    @Override
    default UUID createNewFolder(String userEmail, NewFolderDto newFolder, Boolean desktopFolder) throws NullPointerException, FolderAlreadyExistsException, UnfinishedRepositoryOperationException {
        if (userEmail == null || newFolder == null)
            throw new NullPointerException();
        UUID result;
        boolean exists = this.folderExists(userEmail, newFolder.getFolderName(), newFolder.getFolderId());
        if (exists)
            throw new FolderAlreadyExistsException();
        String folderStatus = this.getNewFolderStatus(newFolder);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        result = switch (folderStatus) {
            case "public-unlocked" -> this.addNewFolder(userEmail, newFolder.getFolderName(), newFolder.getFolderId(), desktopFolder);
            case "public-locked" -> this.addNewFolder(userEmail, newFolder.getFolderName(), newFolder.getFolderId(), desktopFolder, true, bCryptPasswordEncoder.encode(newFolder.getPassword()));
            case "private-unlocked" -> this.addNewFolder(userEmail, newFolder.getFolderName(), newFolder.getFolderId(), desktopFolder, true);
            case "private-locked" -> this.addNewFolder(userEmail, newFolder.getFolderName(), newFolder.getFolderId(), desktopFolder, true, true, bCryptPasswordEncoder.encode(newFolder.getPassword()));
            default -> throw new UnfinishedRepositoryOperationException();
        };
        exists = this.folderExists(userEmail, newFolder.getFolderName(), newFolder.getFolderId());
        if (!exists)
            throw new UnfinishedRepositoryOperationException();
        return result;
    }

    private String getNewFolderStatus(NewFolderDto newFolder) {
        String a = (newFolder.getFolderPrivate())? "private" : "public";
        String b = (newFolder.getFolderHavePassword())? "locked" : "unlocked";
        return (a + "-" + b);
    }

    default List<FolderDto> getRootFolderList(String userEmail, UUID rootFolderId) throws NullPointerException {
        if (userEmail == null)
            throw new NullPointerException();
        return this.findRootSubFolders(userEmail, rootFolderId);
    }

    default UUID getRootFolderId(UUID userId) throws FolderNotFoundException {
        Optional<UUID> result = this.findRootFolderId(userId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        return result.get();
    }

    default UUID getParentFolderId(UUID folderId) throws FolderNotFoundException {
        Optional<UUID> result = this.findParentFolderId(folderId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        return result.get();
    }

    default String getFolderName(UUID folderId) throws NullPointerException, FolderNotFoundException {
        if (folderId == null)
            throw new NullPointerException();
        Optional<String> result = this.findFolderName(folderId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        return result.get();
    }

    default FolderPathDto getFolderPathInfo(UUID folderId) throws NullPointerException, FolderNotFoundException {
        if (folderId == null)
            throw new NullPointerException();
        Optional<FolderPathDto> result = this.findFolderPathInfo(folderId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        return result.get();
    }

    default String getOwnerUsernameByFolderId(UUID folderId) throws NullPointerException, FolderNotFoundException {
        if (folderId == null)
            throw new NullPointerException();
        Optional<String> result = this.findOwnerUsernameByFolderId(folderId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        return result.get();
    }

    default boolean folderPasswordMatch(final UUID folderId, final String rawFolderPassword) throws NullPointerException, FolderNotFoundException {
        if (folderId == null || rawFolderPassword == null)
            throw new NullPointerException();
        Optional<String> result = this.findFolderPassword(folderId);
        if (result.isEmpty())
            throw new FolderNotFoundException();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(rawFolderPassword, result.get());
    }

    default void showFolder(final String userEmail, final UUID folderId) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (userEmail == null || folderId == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.changeFolderVisibilityToPublic(userEmail, folderId);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void hideFolder(final String userEmail, final UUID folderId) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (userEmail == null || folderId == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.changeFolderVisibilityToPrivate(userEmail, folderId);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void setFolderPassword(final UUID folderId, final String encodedPassword) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (folderId == null || encodedPassword == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.setFolderToProtectedWithPassword(folderId, encodedPassword);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void changeFolderPassword(final UUID folderId, final String newEncodedPassword) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (folderId == null || newEncodedPassword == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.changeFolderPasswordByFileId(folderId, newEncodedPassword);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void removeFolderPassword(final UUID folderId) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (folderId == null)
            throw new FolderNotFoundException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.removeFolderPasswordByFileId(folderId);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void changeFolderName(final UUID folderId, final String newFolderName) throws NullPointerException, FolderNotFoundException, UnfinishedRepositoryOperationException {
        if (folderId == null || newFolderName == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        int updatedRows = this.changeFolderNameByFileId(folderId, newFolderName);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void deleteFolder(String userEmail, UUID folderId) throws NullPointerException, FolderNotFoundException, UnauthorizedUserException, UnfinishedRepositoryOperationException {
        if (userEmail == null || folderId == null)
            throw new NullPointerException();
        boolean exists = this.folderExists(folderId);
        if (!exists)
            throw new FolderNotFoundException();
        boolean authorizedUser = this.userIsOwner(userEmail, folderId);
        if (!authorizedUser)
            throw new UnauthorizedUserException();
        this.removeFolder(userEmail, folderId);
        exists = this.folderExists(folderId);
        if (exists)
            throw new UnfinishedRepositoryOperationException();
    }
}
