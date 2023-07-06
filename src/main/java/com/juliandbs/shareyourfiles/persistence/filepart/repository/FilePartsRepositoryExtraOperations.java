package com.juliandbs.shareyourfiles.persistence.filepart.repository;

import com.juliandbs.shareyourfiles.persistence.file.dto.FilePartDto;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
 * This class set the base and extra methods for the FilePartsRepository.
 * @author JulianDbs
 */
@NoRepositoryBean
public interface FilePartsRepositoryExtraOperations {

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM file_parts WHERE file_parts.file_owner_id = :fileOwnerId")
    Optional<Integer> findFilePartCount(@Param("fileOwnerId") UUID fileOwnerId);

    @Query(nativeQuery = true, value = "INSERT INTO file_parts (file_owner_id, file_part_order, file_part_data) VALUES (:fileOwnerId, :filePartOrder, :filePartData) RETURNING file_part_id")
    void addNewFilePart(@Param("fileOwnerId") UUID fileOwnerId, @Param("filePartOrder") Integer filePartOrder, @Param("filePartData") byte[] filePartData);

    @Query(nativeQuery = true)
    Optional<FilePartDto> findFilePart(@Param("filePartId") UUID filePartId);

    @Query(nativeQuery = true, value = "SELECT file_part_id FROM file_parts WHERE file_parts.file_owner_id = :fileOwnerId")
    List<UUID> findFilePartUUIDList(@Param("fileOwnerId") UUID fileOwnerId);

}
