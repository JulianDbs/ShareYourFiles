package com.juliandbs.shareyourfiles.persistence.file.repository;

import com.juliandbs.shareyourfiles.persistence.file.dto.FileInfoDto;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
 * This class set the base and extra methods for the FileRepository.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface FileRepositoryExtraOperations {

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM files WHERE files.file_id = :fileId)")
    boolean fileExists(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM files WHERE files.file_id = :fileId AND files.owner_email = :userEmail)")
    boolean userIsOwner(@Param("userEmail") String userEmail, @Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM files WHERE files.file_is_private = false AND files.file_id = :fileId)")
    boolean fileIsPublic(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM files WHERE files.file_have_password = true AND files.file_id = :fileId)")
    boolean fileHavePassword(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value = "INSERT INTO files (owner_email, folder_id, file_name, file_original_name, file_content_type, file_size, file_is_private, file_have_password, file_password) VALUES (:ownerEmail, :folderId, :fileName, :fileOriginalName, :fileContentType, :fileSize, :fileIsPrivate, :fileHavePassword, :filePassword) RETURNING file_id")
    UUID createFile(@Param("ownerEmail") String ownerEmail,
                                @Param("folderId") UUID folderId,
                                @Param("fileName") String fileName,
                                @Param("fileOriginalName") String fileOriginalName,
                                @Param("fileContentType") String fileContentType,
                                @Param("fileSize") Long fileSize,
                                @Param("fileIsPrivate") Boolean isPrivate,
                                @Param("fileHavePassword") Boolean fileHavePassword,
                                @Param("filePassword") String filePassword);

    @Query(nativeQuery = true)
    List<FileInfoDto> findRootFiles(@Param("userEmail") String userEmail, @Param("rootFolderId") UUID rootFolderId);

    @Query(nativeQuery = true, value = "SELECT file_original_name FROM files WHERE files.file_id = :fileId")
    Optional<String> findFileOriginalName(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true)
    Optional<FileInfoDto> findFileInfoByFileId(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value= "SELECT folder_id FROM files WHERE files.file_id = :fileId")
    Optional<UUID> findFileFolderId(@Param("fileId") UUID fileFolderId);

    @Query(nativeQuery = true, value= "SELECT file_password FROM files WHERE files.file_id = :fileId")
    Optional<String> findFilePassword(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true, value = "SELECT file_content_type FROM files WHERE files.file_id = :fileId")
    Optional<String> findFileContentType(@Param("fileId") UUID fileId);

    @Query(nativeQuery = true)
    List<FileInfoDto> findPublicFilesByFolderId(@Param("folderId") UUID folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value="UPDATE files SET file_is_private = false WHERE files.file_id = :fileId")
    int changeFileToPublicState(@Param("fileId") UUID fileId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value="UPDATE files SET file_is_private = true WHERE files.file_id = :fileId")
    int changeFileToPrivateState(@Param("fileId") UUID fileId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true , value = "UPDATE files SET file_have_password = true, file_password = :encodedPassword WHERE files.file_id = :fileId")
    int setFileToProtectedWithPassword(@Param("fileId") final UUID fileId, @Param("encodedPassword") final String encodedPassword);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true , value = "UPDATE files SET file_have_password = false, file_password = '' WHERE files.file_id = :fileId")
    int removeFilePasswordByFileId(@Param("fileId") final UUID fileId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE files SET file_password = :newEncodedPassword WHERE files.file_id = :fileId")
    int changeFilePasswordByFileId(@Param("fileId") final UUID fileId, @Param("newEncodedPassword") final String newEncodedPassword);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM files WHERE files.file_id = :fileId")
    void removeFile(@Param("fileId") UUID fileId);
}
