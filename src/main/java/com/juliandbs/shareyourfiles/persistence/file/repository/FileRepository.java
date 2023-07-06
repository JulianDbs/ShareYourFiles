package com.juliandbs.shareyourfiles.persistence.file.repository;

import com.juliandbs.shareyourfiles.persistence.file.dto.FileDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileInfoDto;
import com.juliandbs.shareyourfiles.persistence.file.entity.FileEntity;
import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;
import com.juliandbs.shareyourfiles.persistence.file.repository.exception.FileNotFoundException;

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
 * This class is used as a repository to manage the content of the 'files' table.
 * @author JulianDbs
 */
@Repository
public interface FileRepository extends org.springframework.data.repository.Repository<FileEntity, Long>, FileRepositoryExtraOperations {

    default UUID addNewFile(String userEmail, FileDto newFile) throws UnfinishedRepositoryOperationException {
        UUID newFileId = this.createFile(userEmail,
                        newFile.getFolderId(),
                        newFile.getFileName(),
                        newFile.getFileOriginalName(),
                        newFile.getFileContentType(),
                        newFile.getFileSize(),
                        newFile.getFileIsPrivate(),
                        newFile.getFileHavePassword(),
                        newFile.getFilePassword());
        boolean exists = this.fileExists(newFileId);
        if (!exists)
            throw new UnfinishedRepositoryOperationException();
        return newFileId;
    }

    default String getFileOriginalName(UUID fileId) throws NullPointerException, FileNotFoundException {
        if (fileId == null)
            throw new NullPointerException();
        Optional<String> result = this.findFileOriginalName(fileId);
        if (result.isEmpty())
            throw new FileNotFoundException();
        return result.get();
    }

    default FileInfoDto getFileInfo(UUID fileId) throws NullPointerException, FileNotFoundException {
        if (fileId == null)
            throw new NullPointerException();
        Optional<FileInfoDto> result = this.findFileInfoByFileId(fileId);
        if (result.isEmpty())
            throw new FileNotFoundException();
        return result.get();
    }

    default List<FileInfoDto> getRootFileList(String userEmail, UUID rootFolderId) throws NullPointerException {
        if (userEmail == null || rootFolderId == null)
            throw new NullPointerException();
        return this.findRootFiles(userEmail, rootFolderId);
    }

    default UUID getFileFolderId(UUID fileId) throws NullPointerException, FileNotFoundException {
        if (fileId == null)
            throw new NullPointerException();
        Optional<UUID> result = this.findFileFolderId(fileId);
        if (result.isEmpty())
            throw new FileNotFoundException();
        return result.get();
    }

    default String getFileContentType(UUID fileId) throws NullPointerException, FileNotFoundException {
        if (fileId == null)
            throw new NullPointerException();
        Optional<String> result = this.findFileContentType(fileId);
        if (result.isEmpty())
            throw new FileNotFoundException();
        return result.get();
    }

    default boolean filePasswordMatch(final UUID fileId, final String rawFilePassword) throws NullPointerException, FileNotFoundException {
        if (fileId == null || rawFilePassword == null)
            throw new NullPointerException();
        Optional<String> result = this.findFilePassword(fileId);
        if (result.isEmpty())
            throw new FileNotFoundException();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(rawFilePassword, result.get());
    }

    default void setFilePassword(final UUID fileId, final String encodedPassword) throws NullPointerException, FileNotFoundException, UnfinishedRepositoryOperationException {
        if (fileId == null || encodedPassword == null)
            throw new NullPointerException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        int updatedRows = this.setFileToProtectedWithPassword(fileId, encodedPassword);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void changeFilePassword(final UUID fileId, final String newEncodedPassword) throws NullPointerException, FileNotFoundException, UnfinishedRepositoryOperationException {
        if (fileId == null || newEncodedPassword == null)
            throw new NullPointerException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        int updatedRows = this.changeFilePasswordByFileId(fileId, newEncodedPassword);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default void removeFilePassword(final UUID fileId) throws NullPointerException, FileNotFoundException, UnfinishedRepositoryOperationException {
        if (fileId == null)
            throw new FileNotFoundException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        int updatedRows = this.removeFilePasswordByFileId(fileId);
        if (updatedRows == 0)
            throw new UnfinishedRepositoryOperationException();
    }

    default Boolean setFileToPublicState(UUID fileId) throws NullPointerException, FileNotFoundException, UnfinishedRepositoryOperationException {
        if (fileId == null)
            throw new NullPointerException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        int affectedRows = this.changeFileToPublicState(fileId);
        if (affectedRows == 0)
            throw new UnfinishedRepositoryOperationException();
        return this.fileIsPublic(fileId);
    }

    default Boolean setFileToPrivateState(UUID fileId) throws NullPointerException, FileNotFoundException, UnfinishedRepositoryOperationException {
        if (fileId == null)
            throw new NullPointerException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        int affectedRows = this.changeFileToPrivateState(fileId);
        if (affectedRows == 0)
            throw new UnfinishedRepositoryOperationException();
        return !this.fileIsPublic(fileId);
    }

    default Boolean deleteFile(UUID fileId) throws NullPointerException, FileNotFoundException {
        if (fileId == null)
            throw new NullPointerException();
        boolean exists = this.fileExists(fileId);
        if (!exists)
            throw new FileNotFoundException();
        this.removeFile(fileId);
        exists = this.fileExists(fileId);
        return !exists;
    }
}
