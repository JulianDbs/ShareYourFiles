package com.juliandbs.shareyourfiles.persistence.folder.dto;

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
public class FolderRequestDto implements Serializable {

    @JsonProperty("folderId")
    private UUID folderId;

    @JsonProperty("folderPassword")
    private String folderPassword = "";
    public FolderRequestDto() {}

    public FolderRequestDto(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public FolderRequestDto(UUID folderId, String folderPassword) throws NullPointerException {
        if (folderId == null || folderPassword == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.folderPassword = folderPassword;
    }

    @JsonIgnore
    public Boolean isValid() {
        return (folderId != null && folderPassword != null);
    }

    public UUID getFolderId() {return folderId;}

    public void setFolderId(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public String getFolderPassword() {return this.folderPassword;}

    public void setFolderPassword(String folderPassword) throws NullPointerException {
        if (folderPassword == null)
            throw new NullPointerException();
        this.folderPassword = folderPassword;
    }

    @Override
    public int hashCode() {
        return folderId.hashCode();
    }

    @Override
    public String toString() {
        return folderId.toString();
    }
}
