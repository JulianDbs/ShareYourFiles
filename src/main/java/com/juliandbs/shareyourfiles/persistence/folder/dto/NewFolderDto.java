package com.juliandbs.shareyourfiles.persistence.folder.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;

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
@PasswordMatches()
public class NewFolderDto implements Serializable {

    @JsonDeserialize
    @JsonProperty("folderId")
    private UUID folderId;

    @JsonProperty("folderName")
    private String folderName = "";

    @JsonProperty("folderPrivate")
    private Boolean folderPrivate = false;

    @JsonProperty("folderHavePassword")
    private Boolean folderHavePassword = false;

    @JsonProperty("password")
    private String password = "";

    @JsonProperty("matchPassword")
    private String matchPassword = "";

    public NewFolderDto() {}

    public NewFolderDto(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public NewFolderDto(UUID folderId, String folderName) throws NullPointerException {
        if (folderId == null || folderName == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.folderName = folderName;
    }

    public NewFolderDto(UUID folderId, String folderName, Boolean folderPrivate, Boolean folderHavePassword, String password, String matchPassword) throws NullPointerException {
        if (folderId == null ||folderName == null || folderPrivate == null || folderHavePassword == null || password == null || matchPassword ==  null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderPrivate = folderPrivate;
        this.folderHavePassword = folderHavePassword;
        this.password = password;
        this.matchPassword = matchPassword;
    }

    @JsonIgnore
    public Boolean isValid() {
        return (folderId != null && folderName != null && folderPrivate != null && folderHavePassword != null && password != null && matchPassword != null);
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) throws NullPointerException {
        if(folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) throws NullPointerException {
        if (folderName == null )
            throw new NullPointerException();
        this.folderName = folderName;
    }

    public Boolean getFolderPrivate() {
        return folderPrivate;
    }

    public void setFolderPrivate(Boolean folderPrivate) throws NullPointerException {
        if (folderPrivate == null)
            throw new NullPointerException();
        this.folderPrivate = folderPrivate;
    }

    public Boolean getFolderHavePassword() {
        return folderHavePassword;
    }

    public void setFolderHavePassword(Boolean folderHavePassword) throws NullPointerException {
        if (folderHavePassword == null)
            throw new NullPointerException();
        this.folderHavePassword = folderHavePassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws NullPointerException {
        if (password == null)
            throw new NullPointerException();
        this.password = password;
    }

    public String getMatchPassword() {
        return matchPassword;
    }

    public void setMatchPassword(String matchPassword) throws NullPointerException {
        if (matchPassword == null)
            throw new NullPointerException();
        this.matchPassword = matchPassword;
    }

    @Override
    public int hashCode() {
        return folderName.hashCode();
    }

    @Override
    public String toString() {
        return "this is a string";
    }

}
