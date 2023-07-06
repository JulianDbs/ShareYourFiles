package com.juliandbs.shareyourfiles.persistence.user.dto;

import com.juliandbs.shareyourfiles.validation.annotations.ValidUsername;
import com.juliandbs.shareyourfiles.validation.annotations.ValidUsernameCharacters;
import com.juliandbs.shareyourfiles.validation.annotations.ValidUsernameSize;
import com.juliandbs.shareyourfiles.validation.annotations.ValidEmail;
import com.juliandbs.shareyourfiles.validation.annotations.ValidEmailPattern;
import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPasswordSize;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPasswordSize;

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
public class RegistrationFormDto {

    @ValidUsername
    @ValidUsernameCharacters
    @ValidUsernameSize
    private String username;

    @ValidEmail
    @ValidEmailPattern
    private String email;

    @ValidPassword
    @ValidPasswordSize
    private String password;

    @ValidMatchingPassword
    @ValidMatchingPasswordSize
    private String matchingPassword;

    public RegistrationFormDto(){}

    public RegistrationFormDto(String username, String email, String password, String matchingPassword) throws NullPointerException {
        if (username == null || email == null || password ==  null || matchingPassword == null)
            throw new NullPointerException();
        this.username = username;
        this.email = email;
        this.password = password;
        this.matchingPassword = matchingPassword;
    }

    public String getUsername() {return username;}

    public void setUsername(final String username) {this.username = username;}

    public String getEmail() {return email;}

    public void setEmail(final String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(final String password) {this.password = password;}

    public String getMatchingPassword() {return matchingPassword;}

    public void setMatchingPassword(final String matchingPassword) {this.matchingPassword = matchingPassword;}

    @Override
    public String toString() {
        return "Registry(" + "username: " + username + "," +
                "email: " + email + "," +
                "password: " + password + "," +
                "matchingPassword: " + matchingPassword + ")";
    }
}
