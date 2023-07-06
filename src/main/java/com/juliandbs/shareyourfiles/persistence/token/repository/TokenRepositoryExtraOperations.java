package com.juliandbs.shareyourfiles.persistence.token.repository;

import com.juliandbs.shareyourfiles.persistence.token.entity.TokenEntity;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenAlreadyExistsException;
import com.juliandbs.shareyourfiles.persistence.token.repository.exception.TokenNotFoundException;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
 * This interface contains the base methods for the TokenRepository and methods for extra operations.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface TokenRepositoryExtraOperations {

    UUID addNewToken(final UUID userId, final String ipAddress, final LocalDateTime expirationDate) throws NullPointerException, TokenAlreadyExistsException;

    TokenEntity getTokenByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

    LocalDateTime getTokenExpirationDateByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

    UUID getTokenUserIdByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

    void updateExpirationDateByIdAndIpAddress(final UUID tokenId, final String ipAddress, final LocalDateTime newExpirationDate) throws NullPointerException, TokenNotFoundException;

    void deleteTokenByUserIdAndIpAddress(final UUID userId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

    void deleteTokenByIdAndIpAddress(final UUID tokenId, final String ipAddress) throws NullPointerException, TokenNotFoundException;

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM tokens WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress)")
    boolean tokenExistsByTokenId(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM tokens WHERE tokens.user_id = :userId AND tokens.ip_address = :ipAddress)")
    boolean tokenExistsByUserId(@Param("userId") final UUID userId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "INSERT INTO tokens (user_id, ip_address, expiration_date) VALUES (:userId, :ipAddress, :expirationDate) RETURNING token_id")
    UUID createNewToken(@Param("userId") final UUID userId, @Param("ipAddress") final String ipAddress, @Param("expirationDate") final LocalDateTime expirationDate);

    @Query(nativeQuery = true, value = "SELECT * FROM tokens WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress")
    Optional<TokenEntity> findTokenByIdAndIpAddress(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "SELECT token_id FROM tokens WHERE tokens.user_id = :userId AND tokens.ip_address = :ipAddress")
    Optional<UUID> findTokenIdByUserIdAndIpAddress(@Param("userId") final UUID userId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "SELECT expiration_date FROM tokens WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress")
    Optional<LocalDateTime> findTokenExpirationDateByIdAndIpAddress(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "SELECT expiration_date FROM tokens WHERE tokens.user_id = :userId AND tokens.ip_address = :ipAddress")
    Optional<LocalDateTime> findTokenExpirationDateByUserIdAndIpAddress(@Param("userId") final UUID userId, @Param("ipAddress") final String ipAddress);

    @Query(nativeQuery = true, value = "SELECT user_id FROM tokens WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress")
    Optional<UUID> findTokenUserIdByIdAndIpAddress(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE tokens SET expiration_date = :newExpirationDate WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress")
    void changeTokenExpirationDateByIdAndIpAddress(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress, @Param("newExpirationDate") final LocalDateTime newExpirationDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM tokens WHERE tokens.user_id = :userId AND tokens.ip_address = :ipAddress")
    void removeTokenByUserIdAndIpAddress(@Param("userId") final UUID userId, @Param("ipAddress") final String ipAddress);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM tokens WHERE tokens.token_id = :tokenId AND tokens.ip_address = :ipAddress")
    void removeTokenByIdAndIpAddress(@Param("tokenId") final UUID tokenId, @Param("ipAddress") final String ipAddress);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM tokens WHERE tokens.expiration_date <= CURRENT_TIMESTAMP")
    void findAndRemoveExpiredTokens();
}
