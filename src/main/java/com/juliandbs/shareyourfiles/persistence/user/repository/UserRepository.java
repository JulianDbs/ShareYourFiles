package com.juliandbs.shareyourfiles.persistence.user.repository;

import com.juliandbs.shareyourfiles.persistence.user.dto.AccountInfoDto;
import com.juliandbs.shareyourfiles.persistence.user.entity.UserEntity;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.SamePasswordException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.SameUsernameException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UserNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

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
 * This interface is used as a repository to manage the 'users' table and extends the JpaRepository and UserRepositoryExtraOperation interfaces.
 * @author JulianDbs
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryExtraOperations {

    /**
     * This method is used to check if a raw password is the same as the encoded password assigned to the provided user's email.
     * @param userEmail A String instance that represents the user's email.
     * @param rawUserPassword A String instance that represents the raw user password.
     * @return A boolean value that represents the results, true if matches, false if not.
     * @throws NullPointerException If the 'userEmail' or 'rawUserPassword' parameters are null.
     * @throws UserNotFoundException If the user's email does not correspond to an exists user.
     */
    default boolean userPasswordMatch(final String userEmail, final String rawUserPassword) throws NullPointerException, UserNotFoundException {
        if (userEmail == null || rawUserPassword == null)
            throw new NullPointerException();
        Optional<String> result = this.findUserPasswordByEmail(userEmail);
        if (result.isEmpty())
            throw new UserNotFoundException();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(rawUserPassword, result.get());
    }

    /**
     * This method is used to add a new user into the database.
     * @param user A UserEntity class instance that contains the new user data.
     * @return A UUID instance that represents the new user id.
     * @throws UserAlreadyExistsException If already exists a user with the provided user data in the database.
     */
    @Override
    default UUID addNewUser(UserEntity user) throws UserAlreadyExistsException {
        boolean exists = this.userExists(user.getEmail());
        if (exists)
            throw new UserAlreadyExistsException();
        UserEntity result = this.save(user);
        this.flush();
        return result.getUserId();
    }

    /**
     * This method returns a UserEntity containing the user data that corresponds to the provided user email.
     * @param email A String instance that represents the user's email.
     * @return A UserEntity that represents the user data.
     * @throws UserNotFoundException If the database does not contain a user record with the provided user's email.
     */
    default UserEntity getUserByEmail(String email) throws UserNotFoundException {
        Optional<UserEntity> result = this.findUserByEmail(email);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    /**
     * This method returns the username of the user that contains the provided user id.
     * @param userId A UUID instance that represents the user id.
     * @return A String instance that represents the user username.
     * @throws NullPointerException If the 'userId' method argument is null.
     * @throws UserNotFoundException If the provided user id does not correspond to an existing user in the database.
     */
    default String getUsernameByUserId(UUID userId) throws NullPointerException, UserNotFoundException {
        if (userId == null)
            throw new NullPointerException();
        Optional<String> result = this.findUsernameByUserId(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    /**
     * This method returns the user id from a record that contains the provided email.
     * @param userEmail A String instance that represents a user email.
     * @return A UUID instance that represents a user id.
     * @throws NullPointerException If 'userEmail' method argument is null.
     * @throws UserNotFoundException If the 'users' table does not contain any record that uses the provided email.
     */
    default UUID getUserIdByEmail(final String userEmail) throws NullPointerException, UserNotFoundException {
        if (userEmail == null)
            throw new NullPointerException("null object used as 'getUserIdByEmail' method argument.");
        Optional<UUID> result = this.findUserIdByEmail(userEmail);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    /**
     * This method returns the user email from a record that contains the provided user id.
     * @param userId A UUID instance that represents the user id.
     * @return A String instance that represents the user email.
     * @throws NullPointerException If the 'userId' method argument is null.
     * @throws UserNotFoundException If the 'users' table does not contain any record that uses the provided user id.
     */
    default String getUserEmailById(final UUID userId) throws NullPointerException, UserNotFoundException {
        if (userId == null)
            throw new NullPointerException();
        Optional<String> result = this.findUserEmailById(userId);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    /**
     * This method returns a AccountInfoDto that contains user data from the 'users' and 'user_info' tables.
     * @param userEmail A String that represents the user's email.
     * @return A AccountInfoDto that represents user data.
     * @throws NullPointerException If the 'userEmail' method parameter is null.
     * @throws UserNotFoundException If the provided user email does not correspond to an existing user in the database.
     */
    default AccountInfoDto getAccountInfo(final String userEmail) throws NullPointerException, UserNotFoundException {
        if (userEmail == null)
            throw new NullPointerException();
        Optional<AccountInfoDto> result = this.findAccountInfo(userEmail);
        if (result.isEmpty())
            throw new UserNotFoundException();
        return result.get();
    }

    /**
     * This method is used to change the user username.
     * @param email A String instance that represents the user's email.
     * @param newUsername A String instance that represents the new username.
     * @throws UserNotFoundException If the provided user email does not correspond to an existing user in the database.
     * @throws SameUsernameException If the new username is the same as the current username.
     */
    @Override
    default void updateUsernameByEmail(String email, String newUsername) throws UserNotFoundException, SameUsernameException {
        Optional<UserEntity> result = this.findUserByEmail(email);
        if (result.isEmpty())
            throw new UserNotFoundException();
        UserEntity user = result.get();
        if (user.getUsername().equals(newUsername))
            throw new SameUsernameException();
        user.setUsername(newUsername);
        this.saveAndFlush(user);
    }

    /**
     * This method is used to change tha user password.
     * @param email A String instance that represents the user's email.
     * @param newPassword A String instance that represents the new user password.
     * @throws UserNotFoundException If the provided user email does not correspond to an existing user in the database.
     * @throws SamePasswordException If the new user password is the same as the current user password.
     */
    @Override
    default void updatePasswordByEmail(String email, String newPassword) throws UserNotFoundException, SamePasswordException {
        Optional<UserEntity> result = this.findUserByEmail(email);
        if (result.isEmpty())
            throw new UserNotFoundException();
        UserEntity user = result.get();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword()))
            throw new SamePasswordException();
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        this.saveAndFlush(user);
    }

    /**
     * This method is user to delete a user stored in the database.
     * @param email A String instance that represents the user email.
     * @throws UserNotFoundException If the provided user email does not correspond to an existing user in the database.
     */
    default void deleteUserByEmail(String email) throws UserNotFoundException {
        Optional<UserEntity> result = this.findUserByEmail(email);
        if (result.isEmpty())
            throw new UserNotFoundException();
        this.removeUserByEmail(email);
        this.flush();
    }
}