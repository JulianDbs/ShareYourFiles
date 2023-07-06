package com.juliandbs.shareyourfiles.security;

import com.juliandbs.shareyourfiles.persistence.user.entity.UserInfoEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserInfoNotFoundException;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserInfoRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.UserRepository;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
 * This class implements the UserDetailsService interface and is used as a service used by the framework in the login process.
 * @author JulianDbs
 */
@Service
public final class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserInfoRepository userInfoRepository;

    public UserDetailsServiceImpl(final UserRepository userRepository, final UserInfoRepository userInfoRepository) {
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(final String username) throws UsernameNotFoundException {
        UserDetailsImpl result = new UserDetailsImpl();
        try {
            UserEntity user = userRepository.getUserByEmail(username);
            UserInfoEntity userInfo = userInfoRepository.getUserInfoByEmail(username);
            result = new UserDetailsImpl(user, userInfo);
        } catch (UserNotFoundException | UserInfoNotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
        return result;
    }
}