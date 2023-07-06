package com.juliandbs.shareyourfiles.persistence.user.entity;

import com.juliandbs.shareyourfiles.persistence.user.dto.AccountInfoDto;
import jakarta.persistence.*;
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
 * This class is used as an entity that represents the 'users' table in the database.
 * @author JulianDbs
 */
@NamedNativeQuery(
        name="UserEntity.findAccountInfo",
        query="SELECT username, email, (SELECT creation_date FROM user_info WHERE user_info.email = :userEmail) FROM users WHERE users.email = :userEmail",
        resultSetMapping="Mapping.AccountInfoDto")

@SqlResultSetMapping(
        name="Mapping.AccountInfoDto",
        classes=@ConstructorResult(targetClass= AccountInfoDto.class,
                columns= {
                        @ColumnResult(name="username",type=String.class),
                        @ColumnResult(name="email",type=String.class),
                        @ColumnResult(name="creation_date",type=LocalDateTime.class)
                }))

@Entity(name = "users")
@Table(name = "users")
public class UserEntity{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name="user_id", nullable = false)
    private UUID userId;

    @Column(name = "username", nullable = false, length = 21)
    private String username;

    @Column(name = "email", nullable = false, length = 40, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column (name ="user_role", nullable = false, length = 40)
    private String userRole;

    public UserEntity() {}

    public UserEntity(String username, String email, String password) throws NullPointerException {
        if (username == null || email == null || password == null)
            throw new NullPointerException("Null User Class Parameter/s");
        this.userId = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = "USER";
    }

    public UserEntity(UUID userId, String username, String email, String password) throws NullPointerException {
        if (userId == null || username == null || email == null || password == null)
            throw new NullPointerException("Null User Class Parameter/s");
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = "USER";
    }

    public Boolean isValid() {
        return (userId != null && username != null && email != null && password != null);
    }

    public UUID getUserId() {return this.userId;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getUserRole() {return this.userRole;}

    public void setUserRole(String userRole) {this.userRole = userRole;}

    @Override
    public int hashCode() {
        return username.hashCode() + email.hashCode();
    }

    @Override
    public String toString() {return userId + " " + username + " " + email;}
}

