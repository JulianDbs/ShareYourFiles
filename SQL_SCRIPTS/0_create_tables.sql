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

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table roles(
    id serial not null,
    role_name varchar(40) unique not null
);

create table users(
        user_id uuid unique not null DEFAULT uuid_generate_v1(),
        username varchar(21) not null,
        email varchar(40) unique not null,
        password varchar(100) not null,
        user_role varchar(40) not null DEFAULT 'USER',
        constraint fk_role
            foreign key (user_role)
                references roles(role_name)
);

create table user_info(
        id serial not null,
        email varchar(40) not null unique,
        creation_date DATE DEFAULT CURRENT_DATE not null,
        account_expired BOOLEAN DEFAULT FALSE not null,
        account_locked BOOLEAN DEFAULT FALSE not null,
        credentials_expired BOOLEAN DEFAULT FALSE not null,
        account_enabled BOOLEAN DEFAULT TRUE not null,
        constraint fk_email
                foreign key (email)
                        references users(email)
                        on delete cascade
);

CREATE TABLE IF NOT EXISTS tokens (
    token_id uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v1(),
    user_id uuid UNIQUE NOT NULL,
    ip_address VARCHAR(50) NOT NULL,
    expiration_date timestamp NOT NULL,
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            on delete cascade
);

create table folders (
    folder_id uuid unique not null DEFAULT uuid_generate_v1(),
    owner_email varchar(40) not null,
    folder_name varchar(40) not null,
    parent_folder uuid not null,
    desktop_folder BOOLEAN DEFAULT TRUE not null,
    folder_is_private BOOLEAN DEFAULT FALSE not null,
    folder_have_password BOOLEAN DEFAULT FALSE not null,
    folder_password varchar(100) DEFAULT '' not null,
    creation_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint fk_email
        foreign key (owner_email)
            references users(email)
            on delete cascade
);

create table files (
    file_id uuid unique not null DEFAULT uuid_generate_v1(),
    owner_email varchar(40) not null,
    folder_id uuid not null,
    file_name varchar not null,
    file_original_name varchar not null,
    file_content_type varchar not null,
    file_size bigint not null,
    upload_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_is_private BOOLEAN DEFAULT FALSE not null,
    file_have_password BOOLEAN DEFAULT FALSE not null,
    file_password varchar(100) DEFAULT '' not null,
    constraint fk_folder_id
        foreign key (folder_id)
            references folders(folder_id),
    constraint fk_email
        foreign key (owner_email)
            references users(email)
            on delete cascade
);

create table file_parts (
    file_part_id uuid unique not null DEFAULT uuid_generate_v1(),
    file_owner_id uuid not null,
    file_part_order INTEGER not null,
    file_part_data bytea not null,
    constraint fk_file_owner_id
        foreign key (file_owner_id)
            references files(file_id)
            on delete cascade
);