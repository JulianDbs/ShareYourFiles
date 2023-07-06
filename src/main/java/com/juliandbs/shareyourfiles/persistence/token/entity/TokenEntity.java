package com.juliandbs.shareyourfiles.persistence.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;

import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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
 * This class is used as an entity that represents a the 'tokens' table record.
 * @author JulianDbs
 */
@Entity
@Table(name = "tokens")
public class TokenEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "token_id", nullable = false)
    private UUID tokenId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    /**
     * Empty class constructor.
     */
    public TokenEntity() {}

    /**
     * Class constructor.
     * @param tokenId A UUID instance that represents the token id.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @param expirationDate A LocalDateTime instance that represents the token expiration date.
     * @throws NullPointerException If any of the class constructor arguments are null.
     */
    public TokenEntity(final UUID tokenId, final UUID userId, final String ipAddress, final LocalDateTime expirationDate) throws NullPointerException {
        if (tokenId == null || userId == null || ipAddress == null || expirationDate == null)
            throw new NullPointerException();
        this.tokenId = tokenId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.expirationDate = expirationDate;
    }

    /**
     * Class constructor.
     * @param userId A UUID instance that represents the user id.
     * @param ipAddress A String instance that represents the ip address.
     * @param expirationDate A LocalDateTime instance that represents the token expiration date.
     * @throws NullPointerException If any of the class constructor arguments are null.
     */
    public TokenEntity(final UUID userId, final String ipAddress, final LocalDateTime expirationDate) throws NullPointerException {
        if (userId == null || ipAddress == null || expirationDate == null)
            throw new NullPointerException();
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.expirationDate = expirationDate;
    }

    public boolean isValid() {
        return (tokenId != null && userId != null && ipAddress != null && expirationDate != null);
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) throws NullPointerException {
        if (tokenId == null)
            throw new NullPointerException();
        this.tokenId = tokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) throws NullPointerException {
        if (userId == null)
            throw new NullPointerException();
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) throws NullPointerException {
        if (ipAddress == null)
            throw new NullPointerException();
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) throws NullPointerException {
        if (expirationDate == null)
            throw new NullPointerException();
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        return tokenId.hashCode();
    }

    @Override
    public String toString() {
        return tokenId.toString();
    }
}
