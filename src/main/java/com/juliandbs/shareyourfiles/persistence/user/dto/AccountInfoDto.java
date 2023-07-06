package com.juliandbs.shareyourfiles.persistence.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

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
public class AccountInfoDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("creationDate")
    private LocalDateTime creationDate;

    public AccountInfoDto() {}

    public AccountInfoDto(final String username, final String email, final LocalDateTime creationDate) throws NullPointerException {
        if (username == null || email == null || creationDate ==  null)
            throw new NullPointerException();
        this.username = username;
        this.email = email;
        this.creationDate = creationDate;
    }

    public boolean isValid() {
        return (username != null && email != null && creationDate != null);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws NullPointerException {
        if (email == null)
            throw new NullPointerException();
        this.email = email;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) throws NullPointerException {
        if (creationDate == null)
            throw new NullPointerException();
        this.creationDate = creationDate;
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return email;
    }


}
