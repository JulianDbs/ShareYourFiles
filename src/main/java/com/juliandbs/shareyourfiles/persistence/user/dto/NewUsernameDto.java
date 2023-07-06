package com.juliandbs.shareyourfiles.persistence.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juliandbs.shareyourfiles.validation.annotations.*;

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
public class NewUsernameDto {

    @ValidUsername
    @ValidUsernameCharacters
    @ValidUsernameSize
    @JsonProperty("username")
    private String username;

    public NewUsernameDto(){}

    public NewUsernameDto(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        this.username = username;
    }

    @JsonIgnore
    public Boolean isValid() {
        return username != null;
    }

    public String getUsername() {return username;}

    public void setUsername(final String username) {this.username = username;}

    @Override
    public String toString() {
        return username;
    }
}
