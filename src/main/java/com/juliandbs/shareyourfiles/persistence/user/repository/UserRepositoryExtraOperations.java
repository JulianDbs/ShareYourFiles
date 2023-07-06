package com.juliandbs.shareyourfiles.persistence.user.repository;

import com.juliandbs.shareyourfiles.persistence.user.dto.AccountInfoDto;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.SamePasswordException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.SameUsernameException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserNotFoundException;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
 * This interface contains the base methods for the UserRepository and methods for extra operations.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface UserRepositoryExtraOperations {

        @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT 1 FROM users WHERE users.email = :userEmail)")
        boolean userExists(@Param("userEmail") String userEmail);

        UUID addNewUser(UserEntity user) throws UserAlreadyExistsException;

        @Query(nativeQuery = true, value = "SELECT * FROM users WHERE users.email=:email")
        Optional<UserEntity> findUserByEmail(@Param("email") String email);

        @Query(nativeQuery = true, value = "SELECT username FROM users WHERE users.user_id = :userId")
        Optional<String> findUsernameByUserId(@Param("userId") UUID userId);

        @Query(nativeQuery = true, value = "SELECT users.user_id FROM users WHERE users.email = :userEmail")
        Optional<UUID> findUserIdByEmail(@Param("userEmail") final String userEmail);

        @Query(nativeQuery = true, value = "SELECT users.email FROM users WHERE users.user_id = :userId")
        Optional<String> findUserEmailById(@Param("userId") final UUID userId);

        @Query(nativeQuery = true)
        Optional<AccountInfoDto> findAccountInfo(@Param("userEmail") final String userEmail);

        @Query(nativeQuery = true, value = "SELECT password FROM users WHERE users.email = :userEmail")
        Optional<String> findUserPasswordByEmail(@Param("userEmail") final String userEmail);

        void updateUsernameByEmail(String email, String newUsername) throws UserNotFoundException, SameUsernameException;

        void updatePasswordByEmail(String email, String newPassword) throws UserNotFoundException, SamePasswordException;

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Transactional
        @Query(nativeQuery = true, value = "DELETE FROM users WHERE users.email = :userEmail")
        void removeUserByEmail(@Param("userEmail") final String userEmail);
    }

