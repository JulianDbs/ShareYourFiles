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
public class ChangeUsernameRequestDto {

    @JsonProperty("newUsername")
    private String newUsername;

    @JsonProperty("password")
    private String password;

    public ChangeUsernameRequestDto() {}

    public ChangeUsernameRequestDto(final String newUsername, final String password) throws NullPointerException {
        if (newUsername == null || password == null)
            throw new NullPointerException();
        this.newUsername = newUsername;
        this.password = password;
    }

    @JsonIgnore
    public boolean isValid() {
        return (newUsername != null && password != null);
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) throws NullPointerException {
        if (newUsername == null)
            throw new NullPointerException();
        this.newUsername = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws NullPointerException {
        if (password == null)
            throw new NullPointerException();
        this.password = password;
    }

    @Override
    public int hashCode() {
        return newUsername.hashCode();
    }

    @Override
    public String toString() {
        return newUsername;
    }
}
