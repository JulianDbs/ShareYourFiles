package com.juliandbs.shareyourfiles.persistence.folder.entity;

import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderPathDto;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

import java.time.LocalDateTime;
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
 * This class is used as an entity that represents the 'folders' table in the database.
 * @author JulianDbs
 */


@NamedNativeQuery(
        name="FolderEntity.findFolderPathInfo",
        query="SELECT folder_name, parent_folder FROM folders WHERE folders.folder_id = :folderId",
        resultSetMapping="Mapping.FolderPathDto")

@NamedNativeQuery(
        name="FolderEntity.findRootSubFolders",
        query="SELECT folder_id, folder_name, parent_folder, folder_is_private, folder_have_password FROM folders WHERE folders.parent_folder = :rootFolderId AND folders.owner_email = :userEmail ORDER BY creation_date DESC",
        resultSetMapping="Mapping.FolderDto")

@NamedNativeQuery(
        name="FolderEntity.findSubFoldersByFolderId",
        query="SELECT folder_id, folder_name, parent_folder, folder_is_private, folder_have_password FROM folders WHERE folders.folder_id = :folderId AND folders.owner_email = :userEmail",
        resultSetMapping="Mapping.FolderDto")

@NamedNativeQuery(
        name="FolderEntity.findPublicFolderList",
        query="SELECT folder_id, folder_name, parent_folder, folder_is_private, folder_have_password FROM folders WHERE folders.folder_is_private = false AND folder_name != 'root' ORDER BY creation_date DESC",
        resultSetMapping="Mapping.FolderDto")

@NamedNativeQuery(
        name="FolderEntity.findPublicFolderListWithOffset",
        query="SELECT folder_id, folder_name, parent_folder, folder_is_private, folder_have_password FROM folders WHERE folders.folder_is_private = false AND folder_name != 'root' AND desktop_folder = true ORDER BY creation_date DESC LIMIT 20 OFFSET :offset",
        resultSetMapping="Mapping.FolderDto")

@NamedNativeQuery(
        name="FolderEntity.findPublicSubFoldersByFolderId",
        query="SELECT folder_id, folder_name, parent_folder, folder_is_private, folder_have_password FROM folders WHERE folders.parent_folder = :folderId AND folders.folder_is_private = false",
        resultSetMapping="Mapping.FolderDto")


@SqlResultSetMapping(
        name="Mapping.FolderPathDto",
        classes=@ConstructorResult(targetClass= FolderPathDto.class,
                columns= {
                        @ColumnResult(name="folder_name",type=String.class),
                        @ColumnResult(name="parent_folder",type=UUID.class)
                }))

@SqlResultSetMapping(
        name="Mapping.FolderDto",
        classes=@ConstructorResult(targetClass= FolderDto.class,
                columns= {
                        @ColumnResult(name="folder_id",type=UUID.class),
                        @ColumnResult(name="folder_name",type=String.class),
                        @ColumnResult(name="parent_folder",type=UUID.class),
                        @ColumnResult(name="folder_is_private", type=Boolean.class),
                        @ColumnResult(name="folder_have_password", type=Boolean.class)
                }))

@Entity(name = "folders")
@Table(name = "folders")
public class FolderEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "folder_id", nullable = false)
    private UUID folderId;

    @Column(name = "owner_email", nullable = false, length = 40)
    private String ownerEmail;

    @Column(name = "folder_name", nullable = false, length = 40)
    private String folderName;

    @Column(name = "parent_folder", nullable = false, length = 40)
    private UUID parentFolder;

    @Column(name = "desktop_folder", nullable = false)
    private Boolean desktopFolder = true;

    @Column(name = "folder_is_private", nullable = false)
    private Boolean folderIsPrivate = false;

    @Column(name = "folder_have_password", nullable = false)
    private Boolean folderHavePassword = false;

    @Column(name = "folder_password", nullable = false, length = 100)
    private String folderPassword = "";

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    public FolderEntity() {}

    public FolderEntity(UUID folderId, String ownerEmail, String folderName, UUID parentFolder, Boolean desktopFolder, Boolean folderIsPrivate, Boolean folderHavePassword, String folderPassword, LocalDateTime creationDate) throws NullPointerException {
        if (folderId == null || ownerEmail == null || folderName == null || parentFolder == null || desktopFolder == null || folderIsPrivate == null || folderHavePassword == null || folderPassword == null || creationDate == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.ownerEmail = ownerEmail;
        this.folderName = folderName;
        this.parentFolder = parentFolder;
        this.desktopFolder = desktopFolder;
        this.folderHavePassword = folderHavePassword;
        this.folderPassword = folderPassword;
        this.creationDate = creationDate;
    }

    public FolderEntity(String ownerEmail, String folderName, UUID parentFolder, Boolean desktopFolder, Boolean folderIsPrivate, Boolean folderHavePassword, String folderPassword) throws NullPointerException {
        if (ownerEmail == null || folderName == null || parentFolder == null || folderIsPrivate == null || folderHavePassword == null || folderPassword == null)
            throw new NullPointerException();
        this.ownerEmail = ownerEmail;
        this.folderName = folderName;
        this.parentFolder = parentFolder;
        this.desktopFolder = desktopFolder;
        this.folderHavePassword = folderHavePassword;
        this.folderPassword = folderPassword;
    }

    public Boolean isValid() {
        return (folderId != null && ownerEmail != null && folderName != null && parentFolder != null && desktopFolder != null && folderIsPrivate != null && folderHavePassword != null && folderPassword != null && creationDate != null);
    }

    public UUID getFolderId() {return folderId;}

    public String getFolderName() {return this.folderName;}

    public void setFolderName(String folderName) throws NullPointerException {
        if (folderName == null)
            throw new NullPointerException();
        this.folderName = folderName;
    }

    public UUID getParentFolder() {return this.parentFolder;}

    public void setParentFolder(UUID parentFolder) throws NullPointerException {
        if (parentFolder == null)
            throw new NullPointerException();
        this.parentFolder = parentFolder;
    }

    public Boolean getDesktopFolder() {
        return desktopFolder;
    }

    public void setDesktopFolder(Boolean desktopFolder) throws NullPointerException {
        if (desktopFolder == null)
            throw new NullPointerException();
        this.desktopFolder = desktopFolder;
    }

    public Boolean getFolderIsPrivate() {return this.folderIsPrivate;}

    public void setFolderIsPrivate(Boolean folderIsPrivate) throws NullPointerException {
        if (folderIsPrivate == null)
            throw new NullPointerException();
        this.folderIsPrivate = folderIsPrivate;
    }

    public Boolean getFolderHavePassword() {return this.folderHavePassword;}

    public void setFolderHavePassword(Boolean folderHavePassword) throws NullPointerException {
        if (folderHavePassword == null)
            throw new NullPointerException();
        this.folderHavePassword = folderHavePassword;
    }

    public String getFolderPassword() {return this.folderPassword;}

    public void setFolderPassword(String folderPassword) throws NullPointerException {
        if (folderPassword == null)
            throw new NullPointerException();
        this.folderPassword = folderPassword;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) throws NullPointerException {
        if (creationDate == null)
            throw  new NullPointerException();
        this.creationDate = creationDate;
    }

    @Override
    public int hashCode() {
        return (ownerEmail.hashCode() + folderName.hashCode());
    }

    @Override
    public String toString() {
        return (this.parentFolder + "/" + this.folderName);
    }
}
