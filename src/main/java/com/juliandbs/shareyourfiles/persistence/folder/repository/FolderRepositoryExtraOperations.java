package com.juliandbs.shareyourfiles.persistence.folder.repository;

import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderPathDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.repository.exception.FolderAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
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
 * This class set the base and extra methods for the FolderRepository.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface FolderRepositoryExtraOperations {

    @Query(nativeQuery = true, value="SELECT EXISTS (SELECT 1 FROM folders WHERE folders.owner_email = :ownerEmail AND folders.folder_name = :folderName AND folders.parent_folder = :parentFolder)")
    boolean folderExists(@Param("ownerEmail")String ownerEmail, @Param("folderName") String folderName, @Param("parentFolder") UUID parentFolder);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM folders WHERE folders.folder_id = :folderId)")
    boolean folderExists(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value = "SELECT folder_is_private FROM folders WHERE folder_id = :folderId")
    boolean folderIsPrivate(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value ="SELECT folder_have_password FROM folders WHERE folder_id = :folderId")
    boolean folderHavePassword(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM folders WHERE folders.folder_id = :folderId AND folders.owner_email = :userEmail)")
    boolean userIsOwner(@Param("userEmail") String userEmail, @Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value = ("SELECT EXISTS (SELECT 1 FROM folders WHERE folders.owner_email = :userEmail AND folders.parent_folder = :userId)"))
    boolean haveRootFolder(@Param("userEmail") String userEmail, @Param("userId") UUID userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM folders WHERE folders.folder_name != 'root' AND folders.folder_is_private = false")
    long publicFolderCount();

    UUID createNewFolder(String userEmail, NewFolderDto folder, Boolean desktopFolder) throws NullPointerException, FolderAlreadyExistsException, UnfinishedRepositoryOperationException;

    @Query(nativeQuery = true, value = "INSERT INTO folders (owner_email, folder_name, parent_folder) VALUES (:ownerEmail, 'root', :userId) RETURNING folder_id")
    UUID addRootFolder(@Param("ownerEmail") String ownerEmail, @Param("userId") UUID userId);

    @Query(nativeQuery = true, value = "INSERT INTO folders (owner_email, folder_name, parent_folder, desktop_folder) VALUES (:ownerEmail, :folderName, :parentFolder, :desktopFolder) RETURNING folder_id")
    UUID addNewFolder(@Param("ownerEmail") String ownerEmail, @Param("folderName") String folderName, @Param("parentFolder") UUID parentFolder, @Param("desktopFolder") Boolean desktopFolder);

    @Query(nativeQuery = true, value = "INSERT INTO folders (owner_email, folder_name, parent_folder, folder_is_private, desktop_folder) VALUES (:ownerEmail, :folderName, :parentFolder, :folderIsPrivate, :desktopFolder) RETURNING folder_id")
    UUID addNewFolder(@Param("ownerEmail") String ownerEmail, @Param("folderName") String folderName, @Param("parentFolder") UUID parentFolder, @Param("desktopFolder") Boolean desktopFolder, @Param("folderIsPrivate") Boolean folderIsPrivate);

    @Query(nativeQuery = true, value = "INSERT INTO folders (owner_email, folder_name, parent_folder, folder_have_password, folder_password, desktop_folder) VALUES (:ownerEmail, :folderName, :parentFolder, :folderHavePassword, :folderPassword, :desktopFolder) RETURNING folder_id")
    UUID addNewFolder(@Param("ownerEmail") String ownerEmail, @Param("folderName") String folderName, @Param("parentFolder") UUID parentFolder, @Param("desktopFolder") Boolean desktopFolder, @Param("folderHavePassword") Boolean folderHavePassword, @Param("folderPassword") String folderPassword);

    @Query(nativeQuery = true, value = "INSERT INTO folders (owner_email, folder_name, parent_folder, folder_is_private, folder_have_password, folder_password, desktop_folder) VALUES (:ownerEmail, :folderName, :parentFolder, :folderIsPrivate, :folderHavePassword, :folderPassword, :desktopFolder) RETURNING folder_id")
    UUID addNewFolder(@Param("ownerEmail") String ownerEmail, @Param("folderName") String folderName, @Param("parentFolder") UUID parentFolder, @Param("desktopFolder") Boolean desktopFolder,@Param("folderIsPrivate") Boolean folderIsPrivate, @Param("folderHavePassword") Boolean folderHavePassword, @Param("folderPassword") String folderPassword);

    @Query(nativeQuery = true)
    List<FolderDto> findRootSubFolders(@Param("userEmail") String userEmail, @Param("rootFolderId") UUID rootFolderId);

    @Query(nativeQuery = true, value = "SELECT folder_id FROM folders WHERE folders.parent_folder = :userId")
    Optional<UUID> findRootFolderId(@Param("userId") UUID userId);

    @Query(nativeQuery = true, value = "SELECT parent_folder FROM folders WHERE folders.folder_id = :folderId")
    Optional<UUID> findParentFolderId(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value= "SELECT folder_password FROM folders WHERE folders.folder_id = :folderId")
    Optional<String> findFolderPassword(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value = "SELECT folder_name FROM folders WHERE folders.folder_id = :folderId")
    Optional<String> findFolderName(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true)
    Optional<FolderPathDto> findFolderPathInfo(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true)
    List<FolderDto> findPublicFolderList();

    @Query(nativeQuery = true, value = "SELECT folders.folder_password FROM folders WHERE folders.folder_id = :folderId AND folder_is_private = false")
    Optional<String> findPublicFolderPassword(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true)
    LinkedList<FolderDto> findPublicFolderListWithOffset(@Param("offset") Integer offset);

    @Query(nativeQuery = true)
    List<FolderDto> findPublicSubFoldersByFolderId(@Param("folderId") UUID folderId);

    @Query(nativeQuery = true, value = "SELECT username FROM users WHERE users.email = (SELECT owner_email FROM folders WHERE folders.folder_id = :folderId)")
    Optional<String> findOwnerUsernameByFolderId(@Param("folderId") UUID folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE folders SET folder_is_private = false WHERE folders.owner_email = :userEmail AND folders.folder_id = :folderId")
    int changeFolderVisibilityToPublic(@Param("userEmail") final String userEmail, @Param("folderId") final UUID folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE folders SET folder_is_private = true WHERE folders.owner_email = :userEmail AND folders.folder_id = :folderId")
    int changeFolderVisibilityToPrivate(@Param("userEmail") final String userEmail, @Param("folderId") final UUID folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true , value = "UPDATE folders SET folder_have_password = true, folder_password = :encodedPassword WHERE folders.folder_id = :folderId")
    int setFolderToProtectedWithPassword(@Param("folderId") final UUID folderId, @Param("encodedPassword") final String encodedPassword);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true , value = "UPDATE folders SET folder_have_password = false, folder_password = '' WHERE folders.folder_id = :folderId")
    int removeFolderPasswordByFileId(@Param("folderId") final UUID folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE folders SET folder_password = :newEncodedPassword WHERE folders.folder_id = :folderId")
    int changeFolderPasswordByFileId(@Param("folderId") final UUID folderId, @Param("newEncodedPassword") final String newEncodedPassword);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE folders SET folder_name = :newFolderName WHERE folders.folder_id = :folderId")
    int changeFolderNameByFileId(@Param("folderId") final UUID folderId, @Param("newFolderName") final String newFolderName);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM folders WHERE folders.owner_email = :ownerEmail AND folders.folder_id = :folderId")
    void removeFolder(@Param("ownerEmail") String ownerEmail, @Param("folderId") UUID folderId);
}
