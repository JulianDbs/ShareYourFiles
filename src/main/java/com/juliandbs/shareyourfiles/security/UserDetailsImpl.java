package com.juliandbs.shareyourfiles.security;

import com.juliandbs.shareyourfiles.persistence.user.entity.UserEntity;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserInfoEntity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
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
 * This class implements the UserDetails interface and is used to store core user information.
 * @author JulianDbs
 */
public final class UserDetailsImpl implements UserDetails {

    private UserEntity user;

    private UserInfoEntity userInfo;

    public UserDetailsImpl() {}

    public UserDetailsImpl(UserEntity user, UserInfoEntity userInfo) throws NullPointerException {
        if (user == null || userInfo == null) {
            throw new NullPointerException("UserDetailsImpl - null object used as class constructor parameter.");
        }
        this.user = user;
        this.userInfo = userInfo;
    }

    public Boolean isValid() {
        boolean result = false;
        if (user != null && userInfo != null) {
            result = (user.isValid() && userInfo.isValid());
        }
        return result;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority(user.getUserRole()));
    }

    @Override
    public String getPassword() {return user.getPassword();}

    @Override
    public String getUsername() {return user.getUsername();}

    public String getEmailAddress() {return user.getEmail();}

    public UUID getUserId() {return user.getUserId();}

    @Override
    public boolean isAccountNonExpired() {return !userInfo.isExpired();}

    @Override
    public boolean isAccountNonLocked() {return !userInfo.isLocked();}

    @Override
    public boolean isCredentialsNonExpired() {return !userInfo.isCredentialsExpired();}

    @Override
    public boolean isEnabled() {return userInfo.isEnabled();}
}
