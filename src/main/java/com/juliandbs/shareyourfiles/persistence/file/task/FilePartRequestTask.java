package com.juliandbs.shareyourfiles.persistence.file.task;

import com.juliandbs.shareyourfiles.persistence.file.dto.FilePartDto;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.exception.FilePartNotFoundException;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

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
 * This class is used as a callable task by the FileRestService.
 * @author JulianDbs
 */
public class FilePartRequestTask implements Callable<FilePartDto> {

    private final UUID filePartID;

    private final FilePartsRepository filePartsRepository;

    public FilePartRequestTask(UUID filePartID, FilePartsRepository filePartsRepository) throws NullPointerException {
        if (filePartID == null)
            throw new NullPointerException();
        this.filePartID = filePartID;
        this.filePartsRepository = filePartsRepository;
    }

    @Override
    public FilePartDto call() throws FilePartNotFoundException {
        Optional<FilePartDto> result = this.filePartsRepository.findFilePart(this.filePartID);
        if (result.isEmpty())
            throw new FilePartNotFoundException();
        return result.get();
    }
}