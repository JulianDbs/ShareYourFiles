package com.juliandbs.shareyourfiles.persistence.folder.dto;

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
 * A class used as a data transfer object and carry data between process.
 * @author JulianDbs
 */
public class FolderPathDto {

    private String folderName;

    private UUID folderParentId;

    public FolderPathDto() {}

    public FolderPathDto(String folderName, UUID folderParentId) throws NullPointerException {
        if (folderName == null || folderParentId == null)
            throw new NullPointerException();
        this.folderName = folderName;
        this.folderParentId = folderParentId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public UUID getFolderParentId() {
        return folderParentId;
    }

    public void setFolderParentId(UUID folderParentId) {
        this.folderParentId = folderParentId;
    }

    @Override
    public int hashCode() {
        return folderName.hashCode();
    }

    @Override
    public String toString() {
        return this.folderName;
    }
}
