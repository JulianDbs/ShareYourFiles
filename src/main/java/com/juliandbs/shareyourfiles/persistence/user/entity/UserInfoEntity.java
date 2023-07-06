package com.juliandbs.shareyourfiles.persistence.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.lang.NullPointerException;
import java.time.LocalDate;

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
 * This class is used as an entity that represents the 'user_info' table in the database.
 * @author JulianDbs
 */
@Entity()
@Table(name = "user_info")
public class UserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 40, unique = true)
    private String email;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE", name = "creation_date", nullable = false)
    private LocalDate creation_date;

    @Column(columnDefinition = "BOOLEAN DEFAULT false", name = "account_expired", nullable = false)
    private Boolean account_expired;

    @Column(columnDefinition = "BOOLEAN DEFAULT false", name = "account_locked", nullable = false)
    private Boolean account_locked;

    @Column(columnDefinition = "BOOLEAN DEFAULT false", name = "credentials_expired", nullable = false)
    private Boolean credentials_expired;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", name = "account_enabled", nullable = false)
    private Boolean account_enabled;

    public UserInfoEntity() {}

    public UserInfoEntity(String email) throws NullPointerException {
        if (email == null)
            throw new NullPointerException();
        this.email = email;
        creation_date = LocalDate.now();
        account_expired = false;
        account_locked = false;
        credentials_expired = false;
        account_enabled = true;
    }

    public UserInfoEntity(Long id, String email, LocalDate creation_date, Boolean account_expired, Boolean account_locked, Boolean credentials_expired, Boolean account_enabled) throws NullPointerException {
        if (id == null || email == null || creation_date == null || account_expired == null || account_locked == null || credentials_expired == null || account_enabled == null)
            throw new NullPointerException();
        this.id = id;
        this.email = email;
        this.creation_date = creation_date;
        this.account_expired = account_expired;
        this.account_locked = account_locked;
        this.credentials_expired = credentials_expired;
        this.account_enabled = account_enabled;
    }

    public boolean isValid() {
        return id != null && email != null && creation_date != null && account_expired != null && account_locked != null && credentials_expired != null && account_enabled != null;
    }

    public Long getId() {return id;}

    public String getEmail() {return email;}

    public LocalDate getCreationDate() {return creation_date;}

    public boolean isExpired() {return account_expired;}

    public void setAccountExpired(Boolean account_expired) {this.account_expired = account_expired;}

    public boolean isLocked() {return account_locked;}

    public void setAccountLocked(Boolean account_locked) {this.account_locked = account_locked;}

    public boolean isCredentialsExpired() {return credentials_expired;}

    public void setCredentialsExpired(Boolean credentials_expired) {this.credentials_expired = credentials_expired;}

    public boolean isEnabled() {return account_enabled;}

    public void setAccountEnabled(Boolean account_enabled) {this.account_enabled = account_enabled;}

}
