package com.juliandbs.shareyourfiles.persistence.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPasswordSize;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPasswordSize;

import java.lang.NullPointerException;

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
@PasswordMatches
public class NewPasswordDto {

    @ValidPassword
    @ValidPasswordSize
    @JsonProperty("password")
    private String password;

    @ValidMatchingPassword
    @ValidMatchingPasswordSize
    @JsonProperty("matchingPassword")
    private String matchingPassword;

    public NewPasswordDto() {}

    public NewPasswordDto(final String password, final String matchingPassword) throws NullPointerException {
        if (password == null || matchingPassword == null)
            throw new NullPointerException();
        this.password = password;
        this.matchingPassword = matchingPassword;
    }

    @JsonIgnore
    public Boolean isValid() {
        return (password != null && matchingPassword != null);
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getMatchingPassword() {return matchingPassword;}

    public void setMatchingPassword(String matchingPassword) {this.matchingPassword = matchingPassword;}

    @Override
    public int hashCode() {return password.hashCode() + matchingPassword.hashCode();}
}

