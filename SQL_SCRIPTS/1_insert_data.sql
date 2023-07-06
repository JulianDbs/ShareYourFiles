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

insert into roles (role_name) VALUES ('USER'), ('ADMIN');

/* Admin User */
insert into users (user_id, username, email, password, user_role) VALUES ('d779447e-d987-11ed-b0e0-525400c439f6', 'admin', 'admin@admin.com', '$2a$10$iJInfZ/4uznQuZPEKfoccOLcpR.zgOxaFDrpv4broUQPwp0.tzhOe', 'ADMIN');
insert into user_info (email) VALUES ('admin@admin.com');
insert into folders (folder_id, owner_email, folder_name, parent_folder) values ('711de2dc-eaa0-11ed-af9a-525400c439f6', 'admin@admin.com', 'root', 'd779447e-d987-11ed-b0e0-525400c439f6');

/* Normal User*/
insert into users (user_id, username, email, password, user_role) VALUES ('d779447e-d987-11ed-b0e0-525400c43123', 'user', 'user@user.com', '$2a$10$7Q0Mt.LmP31k0NtcBVvuwOXGCJPZ/nY4f481KS9RLOOSn0gzFpIGO', 'USER');
insert into user_info (email) VALUES ('user@user.com');
insert into folders (folder_id, owner_email, folder_name, parent_folder) values ('a42d43f4-f949-11ed-b69a-525400c439f6', 'user@user.com', 'root', 'd779447e-d987-11ed-b0e0-525400c43123');
