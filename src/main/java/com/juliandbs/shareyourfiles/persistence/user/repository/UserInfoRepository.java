package com.juliandbs.shareyourfiles.persistence.user.repository;

import com.juliandbs.shareyourfiles.persistence.user.entity.UserInfoEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserInfoNotFoundException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserInfoAlreadyExistsException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

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
 * This interface is used as a repository to manage the 'user_info' table and extends the JpaRepository and UserInfoRepositoryExtraOperation interfaces.
 * @author JulianDbs
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long>, UserInfoRepositoryExtraOperations {

    @Override
    public default void addNewUserInfo(UserInfoEntity userInfo) throws NullPointerException, UserInfoAlreadyExistsException {
        if (userInfo == null)
            throw new NullPointerException();
        try {
            UserInfoEntity user = this.save(userInfo);
        } catch (DataIntegrityViolationException e) {
            throw new UserInfoAlreadyExistsException();
        }
        this.flush();
    }

    public default UserInfoEntity getUserInfoByEmail(String email) throws NullPointerException, UserInfoNotFoundException {
        if (email == null)
            throw new NullPointerException();
        Optional<UserInfoEntity> result = this.findUserInfoByEmail(email);
        if (!result.isPresent())
            throw new UserInfoNotFoundException();
        return result.get();
    }

    @Override
    public default void updateAccountExpiredByEmail(final String email, final Boolean account_expired) throws NullPointerException, UserInfoNotFoundException {
        if (email == null || account_expired == null)
            throw new NullPointerException();
        UserInfoEntity userInfo = this.getUserInfoByEmail(email);
        userInfo.setAccountExpired(account_expired);
        this.saveAndFlush(userInfo);
    }

    @Override
    public default void updateAccountLockedByEmail(final String email, final Boolean account_locked) throws NullPointerException, UserInfoNotFoundException {
        if (email == null || account_locked == null)
            throw new NullPointerException();
        UserInfoEntity userInfo = this.getUserInfoByEmail(email);
        userInfo.setAccountLocked(account_locked);
        this.saveAndFlush(userInfo);
    }

    @Override
    public default void updateCredentialsExpiredByEmail(final String email, final Boolean credentials_expired) throws NullPointerException, UserInfoNotFoundException {
        if (email == null || credentials_expired == null)
            throw new NullPointerException();
        UserInfoEntity userInfo = this.getUserInfoByEmail(email);
        userInfo.setCredentialsExpired(credentials_expired);
        this.saveAndFlush(userInfo);
    }

    @Override
    public default void updateAccountEnabledByEmail(final String email, final Boolean account_enabled) throws NullPointerException, UserInfoNotFoundException {
        if (email == null || account_enabled == null)
            throw new NullPointerException();
        UserInfoEntity userInfo = this.getUserInfoByEmail(email);
        userInfo.setAccountEnabled(account_enabled);
        this.saveAndFlush(userInfo);
    }

    public default void removeUserInfoByEmail(final String email) throws NullPointerException, UserInfoNotFoundException {
        if (email == null)
            throw new NullPointerException();
        UserInfoEntity userInfo = this.getUserInfoByEmail(email);
        this.deleteUserInfoByEmail(email);
        this.flush();
    }
}
