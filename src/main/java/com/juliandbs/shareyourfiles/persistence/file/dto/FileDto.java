package com.juliandbs.shareyourfiles.persistence.file.dto;

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
 * A class used as a data transfer object and carry data between process.
 * @author JulianDbs
 */
public class FileDto {

    private String ownerEmail;
    private UUID folderId;

    private String fileName;
    private String fileOriginalName;

    private String fileContentType;

    private Long fileSize;

    private LocalDateTime uploadDate;

    private Boolean fileIsPrivate = false;

    private Boolean fileHavePassword = false;

    private String filePassword = "";

    public FileDto() {}

    public FileDto(String ownerEmail, UUID folderId, String fileName, String fileOriginalName,
                   String fileContentType, Long fileSize, LocalDateTime uploadDate,
                   Boolean fileIsPrivate, Boolean fileHavePassword, String filePassword) throws NullPointerException {
        if (ownerEmail == null || folderId == null || fileName == null || fileOriginalName == null ||
                fileContentType == null || fileSize == null || uploadDate == null ||
                fileIsPrivate == null || fileHavePassword == null || filePassword == null)
            throw new NullPointerException();
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

    public FileDto(String ownerEmail, UUID folderId, String fileName, String fileOriginalName, String fileContentType, Long fileSize, Boolean fileIsPrivate, Boolean fileHavePassword, String filePassword) throws NullPointerException {
        if (ownerEmail == null || folderId == null || fileName == null || fileOriginalName == null || fileContentType == null || fileSize == null || fileIsPrivate == null || fileHavePassword == null || filePassword == null)
            throw new NullPointerException();
        this.ownerEmail = ownerEmail;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileContentType = fileContentType;
        this.fileSize = fileSize;
        this.fileIsPrivate = fileIsPrivate;
        this.fileHavePassword = fileHavePassword;
        this.filePassword = filePassword;
    }

    public Boolean isValid() {
        return (ownerEmail != null && folderId != null && fileName != null &&
                fileOriginalName != null && fileContentType != null && fileSize != null &&
                fileIsPrivate != null && fileHavePassword != null && filePassword != null);
    }

    public UUID getFolderId() {return this.folderId;}

    public String getFileName() {return this.fileName;}

    public String getFileOriginalName() {return this.fileOriginalName;}

    public String getFileContentType() {return this.fileContentType;}

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public Boolean getFileIsPrivate() {return this.fileIsPrivate;}

    public Long getFileSize() {return this.fileSize;}

    public Boolean getFileHavePassword() {return this.fileHavePassword;}

    public String getFilePassword() {return this.filePassword;}

    @Override
    public int hashCode() {
        return (ownerEmail.hashCode() + fileName.hashCode());
    }

    @Override
    public String toString() {
        return (this.fileName);
    }

}
