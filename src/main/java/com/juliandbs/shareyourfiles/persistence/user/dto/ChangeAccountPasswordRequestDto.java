package com.juliandbs.shareyourfiles.persistence.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ChangeAccountPasswordRequestDto {

    @JsonProperty("originalPassword")
    private String originalPassword = "";

    @JsonProperty("newPassword")
    private String newPassword = "";

    @JsonProperty("newMatchPassword")
    private String newMatchPassword = "";

    public ChangeAccountPasswordRequestDto() {}

    public ChangeAccountPasswordRequestDto(final String originalPassword, final String newPassword, final String newMatchPassword) throws NullPointerException {
        if (originalPassword == null || newPassword == null || newMatchPassword == null)
            throw new NullPointerException();
        this.originalPassword = originalPassword;
        this.newPassword = newPassword;
        this.newMatchPassword = newMatchPassword;
    }

    @JsonIgnore
    public boolean isValid() {
        return (originalPassword != null && newPassword != null && newMatchPassword != null);
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

    public String getNewMatchPassword() {
        return newMatchPassword;
    }

    public void setNewMatchPassword(String newMatchPassword) throws NullPointerException {
        if (newMatchPassword == null)
            throw new NullPointerException();
        this.newMatchPassword = newMatchPassword;
    }

    @Override
    public int hashCode() {
        return newPassword.hashCode();
    }

    @Override
    public String toString() {
        return "this is a String";
    }

}
