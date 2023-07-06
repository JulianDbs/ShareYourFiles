package com.juliandbs.shareyourfiles.persistence.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
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
public class FileRequestDto implements Serializable {

    @JsonProperty("fileId")
    private UUID fileId;

    @JsonProperty("filePassword")
    private String filePassword;

    @JsonProperty("fileName")
    private String fileName;

    public FileRequestDto() {}

    public FileRequestDto(UUID fileId, String filePassword, String fileName) throws NullPointerException {
        if (fileId == null || filePassword == null || fileName == null)
            throw new NullPointerException();
        this.fileId = fileId;
        this.filePassword = filePassword;
        this.fileName = fileName;
    }

    @JsonIgnore
    public Boolean isValid() {
        return (fileId != null && filePassword != null && fileName != null);
    }

    public UUID getFileId() {return fileId;}

    public void setFileId(UUID fileId) throws NullPointerException {
        if (fileId == null)
            throw new NullPointerException();
        this.fileId = fileId;
    }

    public String getFilePassword() {return this.filePassword;}

    public void setFilePassword(String filePassword) throws NullPointerException {
        if (filePassword == null)
            throw new NullPointerException();
        this.filePassword = filePassword;
    }

    public String getFileName() {return this.fileName;}

    public void setFileName(String fileName) throws NullPointerException {
        if (fileName == null)
            throw new NullPointerException();
        this.fileName = fileName;
    }

    @Override
    public int hashCode() {
        return fileId.hashCode();
    }

    @Override
    public String toString() {
        return fileId.toString();
    }
}
