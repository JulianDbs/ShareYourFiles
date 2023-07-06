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
public class DeleteAccountRequestDto {

    @JsonProperty("password")
    private String password = "";

    @JsonProperty("matchPassword")
    private String matchPassword = "";

    public DeleteAccountRequestDto() {}

    public DeleteAccountRequestDto(final String password, final String matchPassword) throws NullPointerException {
        if (password == null || matchPassword == null)
            throw new NullPointerException();
        this.password = password;
        this.matchPassword = matchPassword;
    }

    @JsonIgnore
    public boolean isValid() {
        return (password != null && matchPassword != null);
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
        return password.hashCode();
    }

    @Override
    public String toString() {
        return "this is a String";
    }
}
