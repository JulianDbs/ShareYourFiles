package com.juliandbs.shareyourfiles.persistence.user.repository;

import com.juliandbs.shareyourfiles.persistence.user.entity.UserInfoEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserInfoAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserInfoNotFoundException;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
 * This interface contains the base methods for the UserInfoRepository and methods for extra operations.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface UserInfoRepositoryExtraOperations {

    //create
    void addNewUserInfo(UserInfoEntity userInfoEntity) throws NullPointerException, UserInfoAlreadyExistsException;

    //read
    @Query(nativeQuery = true, value = "SELECT * FROM user_info WHERE user_info.email=:email")
    Optional<UserInfoEntity> findUserInfoByEmail(@Param("email") String email);

    //update
    void updateAccountExpiredByEmail(final String email, final Boolean account_expired) throws NullPointerException, UserInfoNotFoundException;

    void updateAccountLockedByEmail(final String email, final Boolean account_locked) throws NullPointerException, UserInfoNotFoundException;

    void updateCredentialsExpiredByEmail(final String email, final Boolean credentials_expired) throws NullPointerException, UserInfoNotFoundException;

    void updateAccountEnabledByEmail(final String email, final Boolean account_enabled) throws NullPointerException, UserInfoNotFoundException;

    //delete
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_info WHERE user_info.email=:email")
    void deleteUserInfoByEmail(@Param("email") String email);

}
