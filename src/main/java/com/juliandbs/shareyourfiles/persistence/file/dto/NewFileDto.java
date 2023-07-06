package com.juliandbs.shareyourfiles.persistence.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPassword;
import org.springframework.web.multipart.MultipartFile;

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
public class NewFileDto implements Serializable {

    @JsonProperty("fromUrl")
    private String fromUrl = "";

    @JsonProperty("file")
    private MultipartFile file;

    @JsonProperty("folderId")
    private UUID folderId;

    @JsonProperty("fileIsPrivate")
    private Boolean fileIsPrivate = false;

    @JsonProperty("fileHavePassword")
    private Boolean fileHavePassword = false;

    @JsonProperty("password")
    @ValidPassword
    private String password = "";

    @JsonProperty("matchPassword")
    @ValidMatchingPassword
    private String matchPassword = "";

    public NewFileDto() {}

    public NewFileDto(String fromUrl, UUID folderId) throws NullPointerException {
        if (fromUrl == null || folderId == null)
            throw new NullPointerException();
        this.fromUrl = fromUrl;
        this.folderId = folderId;
    }

    public NewFileDto(String fromUrl, MultipartFile file, UUID folderId, Boolean fileIsPrivate, Boolean fileHavePassword, String password, String matchPassword) throws NullPointerException {
        if (fromUrl == null || file == null || folderId == null || fileIsPrivate == null || fileHavePassword == null || password == null || matchPassword ==  null)
            throw new NullPointerException();
        this.fromUrl = fromUrl;
        this.file = file;
        this.folderId = folderId;
        this.fileIsPrivate = fileIsPrivate;
        this.fileHavePassword = fileHavePassword;
        this.password = password;
        this.matchPassword = matchPassword;
    }

    @JsonIgnore
    public Boolean isValid() {
        return (fromUrl != null && file != null && folderId != null && fileIsPrivate != null && fileHavePassword != null && password != null && matchPassword != null);
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) throws NullPointerException {
        if (fromUrl == null )
            throw new NullPointerException();
        this.fromUrl = fromUrl;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) throws NullPointerException {
        if (file == null)
            throw new NullPointerException();
        this.file = file;
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public Boolean getFileIsPrivate() {
        return fileIsPrivate;
    }

    public void setFileIsPrivate(Boolean fileIsPrivate) throws NullPointerException {
        if (fileIsPrivate == null)
            throw new NullPointerException();
        this.fileIsPrivate = fileIsPrivate;
    }

    public Boolean getFileHavePassword() {
        return fileHavePassword;
    }

    public void setFileHavePassword(Boolean fileHavePassword) throws NullPointerException {
        if (fileHavePassword == null)
            throw new NullPointerException();
        this.fileHavePassword = fileHavePassword;
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
        return file.hashCode();
    }

    @Override
    public String toString() {
        return "this is a string";
    }

}
