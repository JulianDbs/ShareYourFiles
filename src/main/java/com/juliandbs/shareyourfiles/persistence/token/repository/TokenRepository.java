package com.juliandbs.shareyourfiles.persistence.token.repository;

import com.juliandbs.shareyourfiles.persistence.token.entity.TokenEntity;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
 * This interface is used as a repository to manage the 'tokens' table and extends the JpaRepository and TokenRepositoryExtraOperation interfaces.
 * @author JulianDbs
 */
@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID>, TokenRepositoryExtraOperations {

    /**
     * This method is used to add a new token into the database.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @param expirationDate A LocalDateTime instance that represents the token expiration date.
     * @return A UUID instance that represents the id of the new token.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenAlreadyExistsException If already exists a token using the user id and ip address provided.
     */
    @Override
    public default UUID addNewToken(final UUID userId, final String ipAddress, final LocalDateTime expirationDate) throws NullPointerException, TokenAlreadyExistsException {
        if (userId == null || ipAddress == null || expirationDate == null)
            throw new NullPointerException();
        boolean exists = this.tokenExistsByUserId(userId, ipAddress);
        if (exists)
            throw new TokenAlreadyExistsException();
        return this.createNewToken(userId, ipAddress, expirationDate);
    }

    /**
     * This method returns an TokenEntity instance that contains data of the token with the provided token id and ip address.
     * @param tokenId A UUID instance that represents the token id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A TokenEntity instance that represents a token.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the token requested does not exist in the database.
     */
    @Override
    public default TokenEntity getTokenByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        Optional<TokenEntity> result = this.findTokenByIdAndIpAddress(tokenId, ipAddress);
        if (result.isEmpty())
            throw new TokenNotFoundException();
        return result.get();
    }

    /**
     * This method returns a token id from the 'tokens' table that contains the user id and the ip address.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A String instance that represents the token id.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the 'tokens' table does not contain a record that uses the user id and the ip address provided.
     */
    public default String getTokenIdByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        Optional<UUID> result = this.findTokenIdByUserIdAndIpAddress(userId, ipAddress);
        if (result.isEmpty())
            throw new TokenNotFoundException();
        return result.get().toString();
    }

    /**
     * This method returns the token expiration date from the database with the id and ip address provided.
     * @param tokenId A UUID instance that represents the toke id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A LocalDateTime instance that represents the expiration date.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the provided id and ip address.
     */
    public default LocalDateTime getTokenExpirationDateByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        Optional<LocalDateTime> result = this.findTokenExpirationDateByIdAndIpAddress(tokenId, ipAddress);
        if (result.isEmpty())
            throw new TokenNotFoundException();
        return result.get();
    }

    /**
     * This method returns the token expiration date from the database with the user id and ip address provided.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A LocalDateTime instance that represents the expiration date.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the provided user id and ip address.
     */
    public default LocalDateTime getTokenExpirationDateByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        Optional<LocalDateTime> result = this.findTokenExpirationDateByUserIdAndIpAddress(userId, ipAddress);
        if (result.isEmpty())
            throw new TokenNotFoundException();
        return result.get();
    }

    /**
     * This method return the user id of the token in the database that contains the id and ip address provided.
     * @param tokenId A UUID instance that represents the token id.
     * @param ipAddress A String instance that represents the ip address.
     * @return A UUID instance that represents the user id.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the provided id and ip address.
     */
    public default UUID getTokenUserIdByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        Optional<UUID> result = this.findTokenUserIdByIdAndIpAddress(tokenId, ipAddress);
        if (result.isEmpty())
            throw new TokenNotFoundException();
        return result.get();
    }

    /**
     * This method is used to update the expiration date of a token in the database with the id and ip address provided.
     * @param tokenId A UUID instance that represents the token id.
     * @param ipAddress A String instance that represents the ip address.
     * @param newExpirationDate A LocalDateTime instance that represents the new expiration date.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the id and ip address provided.
     */
    public default void updateExpirationDateByIdAndIpAddress(final UUID tokenId, final String ipAddress, final LocalDateTime newExpirationDate) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        boolean exists = this.tokenExistsByTokenId(tokenId, ipAddress);
        if (!exists)
            throw new TokenNotFoundException();
        this.changeTokenExpirationDateByIdAndIpAddress(tokenId, ipAddress, newExpirationDate);
    }

    /**
     * This method is used to delete a token from the database with the user id and ip address provided.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the user id and the ip address provided.
     */
    public default void deleteTokenByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (userId == null || ipAddress == null)
            throw new NullPointerException();
        boolean exists = this.tokenExistsByUserId(userId, ipAddress);
        if (!exists)
            throw new TokenNotFoundException();
        this.removeTokenByUserIdAndIpAddress(userId, ipAddress);
    }

    /**
     * This method is used to delete a token from the database with the token id and ip address provided.
     * @param tokenId A UUID instance that represents the token id.
     * @param ipAddress A String instance that represents the ip address.
     * @throws NullPointerException If any of the method arguments are null.
     * @throws TokenNotFoundException If the database does not contain a token with the id and the ip address provided.
     */
    public default void deleteTokenByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException {
        if (tokenId == null || ipAddress == null)
            throw new NullPointerException();
        boolean exists = this.tokenExistsByTokenId(tokenId, ipAddress);
        if (!exists)
            throw new TokenNotFoundException();
        this.removeTokenByIdAndIpAddress(tokenId, ipAddress);
    }

}
