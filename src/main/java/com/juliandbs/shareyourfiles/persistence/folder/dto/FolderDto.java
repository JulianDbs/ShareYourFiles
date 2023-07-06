package com.juliandbs.shareyourfiles.persistence.folder.dto;

import java.io.Serializable;
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
public final class FolderDto implements Serializable {

    private UUID folderId;

    private String folderName;

    private UUID parentFolder;

    private Boolean folderIsPrivate = false;

    private Boolean folderHavePassword = false;

    public FolderDto() {}

    public FolderDto(UUID folderId, String folderName, UUID parentFolder, Boolean folderIsPrivate, Boolean folderHavePassword) throws NullPointerException {
        if (folderId == null || folderName == null || parentFolder == null || folderIsPrivate == null || folderHavePassword == null)
            throw new NullPointerException();
        this.folderId = folderId;
        this.folderName = folderName;
        this.parentFolder = parentFolder;
        this.folderIsPrivate = folderIsPrivate;
        this.folderHavePassword = folderHavePassword;
    }

    public Boolean isValid() {
        return (folderId != null && folderName != null && parentFolder != null && folderIsPrivate != null && folderHavePassword != null);
    }

    public UUID getFolderId() {return folderId;}

    public FolderRequestDto getFolderRequest() {return new FolderRequestDto(this.folderId);}

    public String getFolderName() {return this.folderName;}

    public UUID getParentFolder() {return this.parentFolder;}

    public Boolean getFolderIsPrivate() {return this.folderIsPrivate;}

    public Boolean getFolderHavePassword() {return this.folderHavePassword;}

    @Override
    public int hashCode() {
        return (folderId.hashCode() + folderName.hashCode());
    }

    @Override
    public String toString() {
        return (this.parentFolder + "/" + this.folderName);
    }

}
