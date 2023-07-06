package com.juliandbs.shareyourfiles.persistence.file.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
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

public class FileInfoDto implements Serializable {

    private UUID fileId;

    private String fileName;

    private String fileOriginalName;

    private String fileContentType;

    private Long fileSize;

    private LocalDateTime uploadDate;

    private Boolean fileIsPrivate = false;

    private Boolean fileHavePassword = false;

    public FileInfoDto() {}

    public FileInfoDto(UUID fileId, String fileName, String fileOriginalName,
                       String fileContentType, Long fileSize, LocalDateTime uploadDate,
                       Boolean fileIsPrivate, Boolean fileHavePassword) throws NullPointerException {
        if (fileId == null || fileName == null || fileOriginalName == null ||
                fileContentType == null || fileSize == null ||
                fileIsPrivate == null || fileHavePassword == null)
            throw new NullPointerException();
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileContentType = fileContentType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.fileIsPrivate = fileIsPrivate;
        this.fileHavePassword = fileHavePassword;
    }

    public Boolean isValid() {
        return (fileId != null && fileName != null &&
                fileOriginalName != null && fileContentType != null && fileSize != null &&
                fileIsPrivate != null && fileHavePassword != null);
    }

    public UUID getFileId() {return fileId;}

    public String getFileName() {return this.fileName;}

    public String getFileOriginalName() {return this.fileOriginalName;}

    public String getFileContentType() {return this.fileContentType;}

    public String getFileSize() {return this.getHumanReadableSize(this.fileSize);}

    public LocalDateTime getFileUploadDate() {
        return uploadDate;
    }

    public Boolean getFileIsPrivate() {return this.fileIsPrivate;}

    public Boolean getFileHavePassword() {return this.fileHavePassword;}

    @Override
    public int hashCode() {

        return (fileId.hashCode() + fileName.hashCode());
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    private String getHumanReadableSize(Long fileSize) {
        String result = "0";
        if (fileSize > 0) {
            final String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int)(Math.log10(fileSize) / Math.log10(1024));
            result = new DecimalFormat("#,##0.#").format(fileSize / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }
        return result;
    }
}
