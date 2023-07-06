package com.juliandbs.shareyourfiles.persistence.file.entity;

import com.juliandbs.shareyourfiles.persistence.file.dto.FileDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileInfoDto;

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
 * This class is used as an entity that represents the 'files' table in the database.
 * @author JulianDbs
 */

@NamedNativeQuery(
        name="FileEntity.findRootFiles",
        query="SELECT file_id , file_name, file_original_name, file_content_type, file_size, upload_date, file_is_private, file_have_password FROM files WHERE files.folder_id = :rootFolderId AND files.owner_email = :userEmail ORDER BY upload_date ASC",
        resultSetMapping="Mapping.FileInfoDto")

@NamedNativeQuery(
        name="FileEntity.findFilesByFolderId",
        query="SELECT file_id, file_name, file_original_name, file_content_type, file_size, upload_date, file_is_private, file_have_password FROM files WHERE files.current_folder = 'root' AND files.folder_id = :folderId ORDER BY upload_date ASC",
        resultSetMapping="Mapping.FileInfoDto")

@NamedNativeQuery(
        name="FileEntity.findFileInfoByFileId",
        query="SELECT file_id, file_name, file_original_name, file_content_type, file_size, upload_date, file_is_private, file_have_password FROM files WHERE files.file_id = :fileId",
        resultSetMapping="Mapping.FileInfoDto")

@NamedNativeQuery(
        name="FileEntity.findPublicFilesByFolderId",
        query="SELECT file_id, file_name, file_original_name, file_content_type, file_size, upload_date, file_is_private, file_have_password FROM files WHERE files.file_is_private = false AND files.folder_id = :folderId ORDER BY upload_date ASC",
        resultSetMapping="Mapping.FileInfoDto")

@SqlResultSetMapping(
        name="Mapping.FileInfoDto",
        classes=@ConstructorResult(targetClass= FileInfoDto.class,
                columns= {
                        @ColumnResult(name="file_id",type=UUID.class),
                        @ColumnResult(name="file_name",type=String.class),
                        @ColumnResult(name="file_original_name",type=String.class),
                        @ColumnResult(name="file_content_type",type=String.class),
                        @ColumnResult(name="file_size",type=Long.class),
                        @ColumnResult(name="upload_date",type=LocalDateTime.class),
                        @ColumnResult(name="file_is_private", type=Boolean.class),
                        @ColumnResult(name="file_have_password", type=Boolean.class)
                }))
    
@Entity(name = "files")
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    @Column(name = "owner_email", nullable = false, length = 40)
    private String ownerEmail;

    @Column(name = "folder_id", nullable = false)
    private UUID folderId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_original_name", nullable = false)
    private String fileOriginalName;

    @Column(name = "file_content_type", nullable = false)
    private String fileContentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;
    
    @Column(name = "file_is_private", nullable = false)
    private Boolean fileIsPrivate = false;

    @Column(name = "file_have_password", nullable = false)
    private Boolean fileHavePassword = false;

    @Column(name = "file_password", nullable = false, length = 100)
    private String filePassword = "";

    public FileEntity() {}

    public FileEntity(UUID fileId, String ownerEmail, UUID folderId, String fileName, String fileOriginalName, String fileContentType, Long fileSize, LocalDateTime uploadDate, Boolean fileIsPrivate, Boolean fileHavePassword, String filePassword) throws NullPointerException {
        if (fileId == null || ownerEmail == null || folderId == null || fileName == null || fileOriginalName == null ||
                fileContentType == null || fileSize == null ||
                fileIsPrivate == null || fileHavePassword == null || filePassword == null)
            throw new NullPointerException();
        this.fileId = fileId;
        this.ownerEmail = ownerEmail;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileContentType = fileContentType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.fileIsPrivate = fileIsPrivate;
        this.fileHavePassword = fileHavePassword;
        this.filePassword = filePassword;
    }

    public FileEntity(String ownerEmail, UUID folderId, String fileName, String fileOriginalName, String fileContentType, Long fileSize) throws NullPointerException {
        if (ownerEmail == null || folderId == null || fileName == null || fileOriginalName == null ||
                fileContentType == null || fileSize == null)
            throw new NullPointerException();
        this.ownerEmail = ownerEmail;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileContentType = fileContentType;
        this.fileSize = fileSize;
    }

    public FileEntity(String ownerEmail, FileDto newFile) throws NullPointerException {
        if (ownerEmail == null || newFile == null || !newFile.isValid())
            throw new NullPointerException();
        this.fileId = UUID.randomUUID();
        this.ownerEmail = ownerEmail;
        this.folderId = newFile.getFolderId();
        this.fileName = newFile.getFileName();
        this.fileOriginalName = newFile.getFileOriginalName();
        this.fileContentType = newFile.getFileContentType();
        this.fileSize = newFile.getFileSize();
        this.uploadDate = LocalDateTime.now();
        this.fileIsPrivate = newFile.getFileIsPrivate();
        this.fileHavePassword = newFile.getFileHavePassword();
        this.filePassword = newFile.getFilePassword();
    }

    public Boolean isValid() {
        return (fileId != null && ownerEmail != null && folderId != null && fileName != null &&
                fileOriginalName != null && fileContentType != null && fileSize != null &&
                fileIsPrivate != null && fileHavePassword != null && filePassword != null);
    }

    public UUID getFileId() {return fileId;}

    public UUID getFolderId() {return this.folderId;}

    public void setFolderId(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public String getFileName() {return this.fileName;}

    public void setFileName(String fileName) throws NullPointerException {
        if (fileName == null)
            throw new NullPointerException();
        this.fileName = fileName;
    }
    
    public String getFileOriginalName() {return this.fileOriginalName;}

    public void setFileOriginalName(String fileOriginalName) throws NullPointerException {
        if (fileOriginalName == null)
            throw new NullPointerException();
        this.fileOriginalName = fileOriginalName;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) throws NullPointerException {
        if (uploadDate == null)
            throw new NullPointerException();
        this.uploadDate = uploadDate;
    }

    public Boolean getFileIsPrivate() {return this.fileIsPrivate;}

    public void setFileIsPrivate(Boolean fileIsPrivate) throws NullPointerException {
        if (fileIsPrivate == null)
            throw new NullPointerException();
        this.fileIsPrivate = fileIsPrivate;
    }

    public Boolean getFileHavePassword() {return this.fileHavePassword;}

    public void setFileHavePassword(Boolean fileHavePassword) throws NullPointerException {
        if (fileHavePassword == null)
            throw new NullPointerException();
        this.fileHavePassword = fileHavePassword;
    }

    public String getFilePassword() {return this.filePassword;}

    public void setFilePassword(String filePassword) throws NullPointerException {
        if (filePassword == null)
            throw new NullPointerException();
        this.filePassword = filePassword;
    }

    @Override
    public int hashCode() {
        return (ownerEmail.hashCode() + fileName.hashCode());
    }

    @Override
    public String toString() {
        return (this.fileName);
    }


}
