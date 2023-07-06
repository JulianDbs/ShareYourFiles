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
public class ChangeFolderPasswordDto implements Serializable {

    private UUID folderId;

    private String originalPassword = "";

    @ValidPassword
    private String newPassword = "";

    @ValidMatchingPassword
    private String matchNewPassword = "";

    public ChangeFolderPasswordDto() {}

    public ChangeFolderPasswordDto(final UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public ChangeFolderPasswordDto(final UUID folderId, final String originalPassword, final String newPassword, final String matchNewPassword) throws NullPointerException {
        if (folderId == null || originalPassword == null || newPassword == null || matchNewPassword == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.originalPassword = originalPassword;
        this.newPassword = newPassword;
        this.matchNewPassword = matchNewPassword;
    }

    public Boolean isValid() {
        return (folderId != null && originalPassword != null && newPassword != null && matchNewPassword != null);
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) throws NullPointerException {
        if (folderId == null)
            throw new NullPointerException();
        this.folderId = folderId;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) throws NullPointerException {
        if (originalPassword == null)
            throw new NullPointerException();
        this.originalPassword = originalPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) throws NullPointerException {
        if (newPassword == null)
            throw new NullPointerException();
        this.newPassword = newPassword;
    }

    public String getMatchNewPassword() {
        return matchNewPassword;
    }

    public void setMatchNewPassword(String matchNewPassword) throws NullPointerException {
        if (matchNewPassword == null)
            throw new NullPointerException();
        this.matchNewPassword = matchNewPassword;
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
