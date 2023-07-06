package com.juliandbs.shareyourfiles.persistence.folder.dto;

import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPassword;

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
public class NewFolderPasswordDto implements Serializable {

    private UUID folderId;

    @ValidPassword
    private String password = "";

    @ValidMatchingPassword
    private String matchPassword = "";

    public NewFolderPasswordDto() {}

    public NewFolderPasswordDto(final UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public NewFolderPasswordDto(final UUID folderId, final String password, final String matchPassword) throws NullPointerException {
        if (folderId == null || password == null || matchPassword == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.password = password;
        this.matchPassword = matchPassword;
    }

    public Boolean isValid() {
        return (folderId != null && password != null && matchPassword != null);
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
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
        return folderId.hashCode();
    }

    @Override
    public String toString() {
        return "this is a string";
    }

}
