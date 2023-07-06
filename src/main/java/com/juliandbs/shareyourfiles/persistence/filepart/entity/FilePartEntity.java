package com.juliandbs.shareyourfiles.persistence.filepart.entity;

import com.juliandbs.shareyourfiles.persistence.file.dto.FilePartDto;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
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
 * This class is used as an entity that represents the 'file_parts' table in the database.
 * @author JulianDbs
 */

@NamedNativeQuery(
        name="FilePartEntity.findFileParts",
        query="SELECT file_part_order, file_part_data FROM file_parts WHERE file_parts.file_owner_id = :fileOwnerId",
        resultSetMapping="Mapping.FilePartDto")

@NamedNativeQuery(
        name="FilePartEntity.findFilePart",
        query="SELECT file_part_order, file_part_data FROM file_parts WHERE file_parts.file_part_id = :filePartId",
        resultSetMapping="Mapping.FilePartDto")

@SqlResultSetMapping(
        name="Mapping.FilePartDto",
        classes=@ConstructorResult(targetClass= FilePartDto.class,
                columns= {
                        @ColumnResult(name="file_part_order",type=Integer.class),
                        @ColumnResult(name="file_part_data",type=byte[].class)
                }))

@Entity(name = "file_parts")
@Table(name = "file_parts")
public class FilePartEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "file_part_id", nullable = false)
    private UUID filePartId;

    @Column(name = "file_owner_id", nullable = false)
    private UUID fileOwnerId;

    @Column(name = "file_part_order", nullable = false)
    private Integer filePartOrder;

    @Column(name = "file_part_data", nullable = false)
    private byte[] filePartData;

    public FilePartEntity() {}

    public FilePartEntity(UUID filePartId, UUID fileOwnerId, Integer filePartOrder, byte[] filePartData) throws NullPointerException {
        if (filePartId == null || fileOwnerId == null || filePartOrder == null || filePartData == null)
            throw new NullPointerException();
        this.filePartId = filePartId;
        this.fileOwnerId = fileOwnerId;
        this.filePartOrder = filePartOrder;
        this.filePartData = filePartData;
    }

    public Boolean isValid() {
        return (filePartId != null && fileOwnerId != null && filePartOrder != null && filePartData != null);
    }

    public UUID getFilePartId() {return this.filePartId;}

    public void setFilePartId(UUID filePartId) throws NullPointerException {
        if (filePartId == null)
            throw new NullPointerException();
        this.filePartId = filePartId;
    }

    public UUID getFileOwnerId() {return this.fileOwnerId;}

    public void setFileOwnerId(UUID fileOwnerId) throws NullPointerException {
        if (fileOwnerId == null)
            throw new NullPointerException();
        this.fileOwnerId = fileOwnerId;
    }

    public Integer getFIlePartOrder() {return this.filePartOrder;}

    public void setFilePartOrder(Integer filePartOrder) throws NullPointerException {
        if (filePartOrder == null)
            throw new NullPointerException();
        this.filePartOrder = filePartOrder;
    }

    public byte[] getFilePartData() {return this.filePartData;}

    public void setFilePartData(byte[] filePartData) throws NullPointerException {
        if (filePartData == null)
            throw new NullPointerException();
        this.filePartData = filePartData;
    }

    @Override
    public int hashCode() {
        return fileOwnerId.hashCode();
    }

    @Override
    public String toString() {
        return (filePartOrder.toString() + " | " + fileOwnerId.toString());
    }
}
