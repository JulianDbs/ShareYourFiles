package com.juliandbs.shareyourfiles.persistence.filepart.repository;

import com.juliandbs.shareyourfiles.persistence.file.dto.FilePartDto;
import com.juliandbs.shareyourfiles.persistence.filepart.entity.FilePartEntity;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.exception.FilePartNotFoundException;

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
 * This class is used as a repository to manage the content of the 'file_parts' table.
 * @author JulianDbs
 */
@Repository
public interface FilePartsRepository extends org.springframework.data.repository.Repository<FilePartEntity, UUID>, FilePartsRepositoryExtraOperations {

    default void createNewFilePart(UUID fileOwnerId, FilePartDto filePart) throws NullPointerException {
        if (fileOwnerId == null || filePart == null)
            throw new NullPointerException();
        this.addNewFilePart(fileOwnerId, filePart.getFilePartOrder(), filePart.getFilePartData());
    }

    default Integer getFilePartCount(UUID fileOwnerId) throws NullPointerException, FilePartNotFoundException {
        Optional<Integer> result = this.findFilePartCount(fileOwnerId);
        if (result.isEmpty())
            throw new FilePartNotFoundException();
        return result.get();
    }
}
