package com.juliandbs.shareyourfiles.persistence.file.service;

import com.juliandbs.shareyourfiles.persistence.base.BaseService;
import com.juliandbs.shareyourfiles.persistence.base.exceptions.UnfinishedRepositoryOperationException;
import com.juliandbs.shareyourfiles.persistence.file.dto.*;
import com.juliandbs.shareyourfiles.persistence.file.repository.FileRepository;
import com.juliandbs.shareyourfiles.persistence.file.repository.exception.FileNotFoundException;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.*;
import com.juliandbs.shareyourfiles.persistence.file.task.FilePartRequestTask;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.CorruptedByteArrayDataException;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.exception.FilePartNotFoundException;
import com.juliandbs.shareyourfiles.persistence.file.service.exception.FilePartNotSavedException;
import com.juliandbs.shareyourfiles.persistence.filepart.repository.FilePartsRepository;
import com.juliandbs.shareyourfiles.persistence.filepart.tool.ByteArrayTools;
import com.juliandbs.shareyourfiles.persistence.filepart.tool.FilePartTools;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.FolderNotExistsException;
import com.juliandbs.shareyourfiles.persistence.folder.service.exception.ForbiddenFolderAccessException;
import com.juliandbs.shareyourfiles.persistence.folder.repository.FolderRepository;
import com.juliandbs.shareyourfiles.persistence.user.repository.exception.UnauthenticatedUserException;
import com.juliandbs.shareyourfiles.tools.PropertyImporter;
import com.juliandbs.shareyourfiles.tools.RestResponse;

import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

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
 * This class extends the BaseService and is used as a service that manages the file rest requests.
 * @author JulianDbs
 */
@Service("fileRestService")
public final class FileRestService extends BaseService implements FileRestServiceI {

    private final long MAX_FILE_SIZE;

    private static final Integer FILE_PART_SIZE = ByteArrayTools.getMegaByteCountInByteLength(200);

    private static final int MAX_FILE_PASSWORD_LENGTH = 50;

    private static final int MIN_FILE_PASSWORD_LENGTH = 6;

    public FileRestService(final MessageSource messageSource, final FolderRepository folderRepository, final FileRepository fileRepository, final FilePartsRepository filePartsRepository, final Environment environment) {
        super(messageSource, folderRepository, fileRepository, filePartsRepository);
        this.MAX_FILE_SIZE = PropertyImporter.getMaxFileSizeProperty(environment);
    }

    /**
     * This method is used to process the '/file/add-file' PUT rest endpoint requests and add a new file into the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param newFile A NewFileDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws InvalidNewFileRequestException If the method arguments are null or the NewFileDto instance is not valid.
     * @throws EmptyNewFileException If the MultipartFile class instance that contains the file data, inside NewFileDto class, is empty.
     * @throws FolderNotExistsException If the requested folder to store the new file do not exist.
     * @throws ForbiddenFolderAccessException If the current user is not the owner of the requested folder to store the new file.
     * @throws FileUploadFailureException If the file repository fails to add the new file into the database.
     * @throws FileIsNotValidException  If the content of the MultipartFile class instance, in the NewFileDto class, contains a corrupted byte array.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws FilePasswordsDoesNotMatchException If the file and match file passwords does not match.
     * @throws NewFileIsToBigException Is the new file size is greater than the max file size established in the application_x.properties file.
     */
    @Override
    public ResponseEntity<Object> storeNewFile(Authentication authentication, NewFileDto newFile)
            throws UnauthenticatedUserException,
            InvalidNewFileRequestException,
            EmptyNewFileException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FileUploadFailureException,
            FileIsNotValidException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            FilePasswordsDoesNotMatchException,
            NewFileIsToBigException {
        this.validateStoreFileRequest(authentication, newFile);
        try {
            UUID folderId = newFile.getFolderId();
            String emailAddress = this.getRestUserEmailAddress(authentication);
            UUID newFileId = this.saveNewFile(emailAddress, folderId, newFile);
            String successMessage = this.getLocalizedMessage("lang.message.file-upload-successfully");
            return RestResponse.build(successMessage, HttpStatus.OK, newFileId);
        } catch (UnfinishedRepositoryOperationException e) {
            throw new FileUploadFailureException();
        } catch (CorruptedByteArrayDataException e) {
            throw new FileIsNotValidException();
        }
    }

    private void validateStoreFileRequest(final Authentication authentication, final NewFileDto newFile)
        throws UnauthenticatedUserException,
            InvalidNewFileRequestException,
            EmptyNewFileException,
            FolderNotExistsException,
            ForbiddenFolderAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            FilePasswordsDoesNotMatchException,
            NewFileIsToBigException {
                this.validateRestAuthentication(authentication);
        if (!newFile.isValid())
            throw new InvalidNewFileRequestException();
        if (newFile.getFile().isEmpty())
            throw new EmptyNewFileException();
        if (newFile.getFileHavePassword()) {
            if (newFile.getPassword().length() < MIN_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToShortException();
            if (newFile.getPassword().length() > MAX_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToLongException();
            if (newFile.getFileHavePassword() && !newFile.getPassword().equals(newFile.getMatchPassword()))
                throw new FilePasswordsDoesNotMatchException();
        }
        UUID folderId = newFile.getFolderId();
        String emailAddress = this.getRestUserEmailAddress(authentication);
        boolean folderExists = this.folderRepository.folderExists(folderId);
        if (!folderExists)
            throw new FolderNotExistsException();
        boolean folderAccess = this.folderRepository.userIsOwner(emailAddress, folderId);
        if (!folderAccess)
            throw new ForbiddenFolderAccessException();
        long newFileSize = newFile.getFile().getSize();
        if (newFileSize > MAX_FILE_SIZE)
            throw new NewFileIsToBigException();
    }

    private UUID saveNewFile(String emailAddress, UUID folderId, NewFileDto newFile) throws CorruptedByteArrayDataException, UnfinishedRepositoryOperationException {
        FileDto fileToUpload = this.buildFileDto(emailAddress, folderId, newFile);
        UUID newFileId;
        byte[] fileData;
        try {
            fileData = newFile.getFile().getBytes();
        } catch (IOException e) {
            throw new CorruptedByteArrayDataException();
        }
        try {
            newFileId = this.fileRepository.addNewFile(emailAddress, fileToUpload);
        } catch (UnfinishedRepositoryOperationException e) {
            throw new UnfinishedRepositoryOperationException();
        }
        boolean operationFailed = false;
        try {
            List<FilePartDto> filePartList = FilePartTools.splitByteArrayIntoFilePartList(FILE_PART_SIZE, fileData);
            this.processFileParts(newFileId, filePartList);
        } catch (FilePartNotFoundException | FilePartNotSavedException e) {
            operationFailed = true;
        }
        if (operationFailed) {
            try {
                this.fileRepository.deleteFile(newFileId);
            } catch (FileNotFoundException e) {
                throw new UnfinishedRepositoryOperationException(e);
            }
            throw new UnfinishedRepositoryOperationException();
        }
        return newFileId;
    }

    private void processFileParts(UUID newFileId, List<FilePartDto> filePartList) throws FilePartNotFoundException,  FilePartNotSavedException {
        filePartList.parallelStream().forEach( part -> this.filePartsRepository.createNewFilePart(newFileId, part));
        int filePartsAdded = this.filePartsRepository.getFilePartCount(newFileId);
        if (filePartsAdded != filePartList.size())
            throw new FilePartNotSavedException();
    }

    /**
     * This method is used to process the '/file/add-file' PUT rest endpoint requests and returns a file data from the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param fileRequest A FileRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that contains a byte array representing the file content data.
     * @throws InvalidFileRequestException If the method arguments are null or the FileRequestDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the requested file is private and the current user is not the owner.
     * @throws InterruptedFileRequestException If an internal error occurs while joining the data parts of the file.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws InvalidFilePasswordException If the provided file password is not the current file password.
     */
    @Override
    public ResponseEntity<byte[]> getFileResource(Authentication authentication, FileRequestDto fileRequest)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            InvalidFilePasswordException {
        this.validateGetFileRequest(authentication, fileRequest);
        UUID fileId = fileRequest.getFileId();
        boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
        if (fileHavePassword) {
            return this.getResponseFileWithPassword(fileId, fileRequest.getFilePassword());
        } else {
            return this.getResponseFile(fileId);
        }
    }

    private void validateGetFileRequest(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            InvalidFilePasswordException {
        if (authentication == null || fileRequest == null || !fileRequest.isValid())
            throw new InvalidFileRequestException();
        this.validateRestAuthentication(authentication);
        String emailAddress = this.getRestUserEmailAddress(authentication);
        UUID fileId = fileRequest.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        boolean fileIsPublic = this.fileRepository.fileIsPublic(fileId);
        if (!fileIsPublic)
            this.checkRestFileOwner(emailAddress, fileId);
        boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
        if (fileHavePassword) {
            if (fileRequest.getFilePassword().length() < MIN_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToShortException();
            if (fileRequest.getFilePassword().length() > MAX_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToLongException();
            try {
                boolean validPassword = this.fileRepository.filePasswordMatch(fileId, fileRequest.getFilePassword());
                if (!validPassword)
                    throw new InvalidFilePasswordException();
            } catch (FileNotFoundException e) {
                throw new FileNotExistsException();
            }
        }
    }

    private ResponseEntity<byte[]> getResponseFileWithPassword(UUID fileId, String filePassword) throws FileNotExistsException, ForbiddenFileAccessException, InterruptedFileRequestException {
        Optional<String> originalPassword = this.fileRepository.findFilePassword(fileId);
        if (originalPassword.isEmpty())
            return ResponseEntity.badRequest().build();
        boolean passwordMatches = this.passwordEncoder().matches(filePassword, originalPassword.get());
        byte[] data;
        String fileOriginalName;
        String fileContentType;
        if (passwordMatches) {
            try {
                List<UUID> uuidList = this.filePartsRepository.findFilePartUUIDList(fileId);
                List<FilePartDto> filePartList = this.buildFilePartList(uuidList);
                filePartList.sort(Comparator.comparing(FilePartDto::getFilePartOrder));
                data = FilePartTools.joinFileParts(filePartList);
                uuidList.clear();
                filePartList.clear();
                fileOriginalName = this.fileRepository.getFileOriginalName(fileId);
                fileContentType = this.fileRepository.getFileContentType(fileId);
            } catch (FileNotFoundException e) {
                throw new FileNotExistsException();
            } catch (InterruptedException | ExecutionException e ) {
                throw new InterruptedFileRequestException();
            }
        } else {
            throw new ForbiddenFileAccessException();
        }
        return buildResponseEntityFromByteArray(fileOriginalName, fileContentType, data);
    }

    private ResponseEntity<byte[]> getResponseFile(UUID fileId) throws FileNotExistsException, InterruptedFileRequestException {
        byte[] data;
        String fileOriginalName;
        String fileContentType;
        try {
            List<UUID> uuidList = this.filePartsRepository.findFilePartUUIDList(fileId);
            List<FilePartDto> filePartList = this.buildFilePartList(uuidList);
            filePartList.sort(Comparator.comparing(FilePartDto::getFilePartOrder));
            data = FilePartTools.joinFileParts(filePartList);
            uuidList.clear();
            filePartList.clear();
            fileOriginalName = this.fileRepository.getFileOriginalName(fileId);
            fileContentType = this.fileRepository.getFileContentType(fileId);
        } catch (InterruptedException | ExecutionException e) {
            throw new InterruptedFileRequestException();
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        }
        return buildResponseEntityFromByteArray(fileOriginalName, fileContentType, data);
    }

    private List<FilePartDto> buildFilePartList(List<UUID> uuidList) throws InterruptedException, ExecutionException {
        List<FilePartDto> result = new LinkedList<>();
        List<Future<FilePartDto>> futureList = new LinkedList<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (UUID filePartId : uuidList) {
            FilePartRequestTask requestTask = new FilePartRequestTask(filePartId, this.filePartsRepository);
            Future<FilePartDto> futureResult = executor.submit(requestTask);
            futureList.add(futureResult);
        }
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        for (Future<FilePartDto> futureResult : futureList) {
            result.add(futureResult.get());
        }
        return result;
    }

    private ResponseEntity<byte[]> buildResponseEntityFromByteArray(String fileOriginalName, String fileContentType, byte[] data) {
        String header = "attachment; filename=\"" + fileOriginalName + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .contentLength(data.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, header)
                .body(data);
    }

    /**
     * This method is used to process the '/file/show-file' PATCH rest endpoint requests and change the private state of a file in the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param fileRequest A FileRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws InvalidShowFileRequestException If the method arguments are null or the FileRequestDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws InvalidFilePasswordException If the provided password and the file password does not match.
     * @throws InterruptedFileRequestException If the file repository does not apply the requested changes.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> showFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidShowFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException {
        this.validateShowFileRequest(authentication, fileRequest);
        UUID fileId = fileRequest.getFileId();
        try {
            this.fileRepository.setFileToPublicState(fileId);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFileRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.set-file-public-state-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateShowFileRequest(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidShowFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException {
        if (authentication == null || fileRequest == null || !fileRequest.isValid())
            throw new InvalidShowFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = fileRequest.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        try {
            boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
            if (fileHavePassword) {
                if (fileRequest.getFilePassword().length() < MIN_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToShortException();
                if (fileRequest.getFilePassword().length() > MAX_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToLongException();
                boolean validOriginalPassword = this.fileRepository.filePasswordMatch(fileId, fileRequest.getFilePassword());
                if (!validOriginalPassword)
                    throw new InvalidFilePasswordException();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        }
    }

    /**
     * This method is used to process the '/file/hide-file' PATCH rest endpoint requests and change the private state of a file in the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param fileRequest A FileRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws InvalidHideFileRequestException If the method arguments are null or the FileRequestDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws InvalidFilePasswordException If the provided password and the file password does not match.
     * @throws InterruptedFileRequestException If the file repository does not apply the requested changes.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> hideFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidHideFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException {
        this.validateHideFileRequest(authentication, fileRequest);
        UUID fileId = fileRequest.getFileId();
        try {
            this.fileRepository.setFileToPrivateState(fileId);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFileRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.set-file-private-state-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateHideFileRequest(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidHideFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            InvalidFilePasswordException {
        if (authentication == null || fileRequest == null || !fileRequest.isValid())
            throw new InvalidHideFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = fileRequest.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        try {
            boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
            if (fileHavePassword) {
                if (fileRequest.getFilePassword().length() < MIN_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToShortException();
                if (fileRequest.getFilePassword().length() > MAX_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToLongException();
                boolean validOriginalPassword = this.fileRepository.filePasswordMatch(fileId, fileRequest.getFilePassword());
                if (!validOriginalPassword)
                    throw new InvalidFilePasswordException();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        }
    }

    /**
     * This method is used to process the '/file/set-password' PATCH rest endpoint requests and set a password to a file in the database.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param newFilePassword A NewFilePasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws InvalidFileRequestException If the method arguments are null or the NewFilePasswordDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws FileAlreadyHavePasswordException If the requested file already have a password.
     * @throws FilePasswordsDoesNotMatchException If the password and match password are not equals.
     * @throws InterruptedFileRequestException If the file repository does not apply the requested changes.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> setFilePassword(final Authentication authentication, final NewFilePasswordDto newFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FileAlreadyHavePasswordException,
            FilePasswordsDoesNotMatchException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException {
        this.validateSetFilePasswordRequest(authentication, newFilePassword);
        UUID fileId = newFilePassword.getFileId();
        String encodedPassword = this.passwordEncoder().encode(newFilePassword.getPassword());
        try {
            this.fileRepository.setFilePassword(fileId, encodedPassword);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFileRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.set-file-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateSetFilePasswordRequest(final Authentication authentication, final NewFilePasswordDto newFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FileAlreadyHavePasswordException,
            FilePasswordsDoesNotMatchException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException {
        if (authentication == null || newFilePassword == null || !newFilePassword.isValid())
            throw new InvalidFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = newFilePassword.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
        if (fileHavePassword)
            throw new FileAlreadyHavePasswordException();
        if (newFilePassword.getPassword().length() < MIN_FILE_PASSWORD_LENGTH)
            throw new FilePasswordIsToShortException();
        if (newFilePassword.getPassword().length() > MAX_FILE_PASSWORD_LENGTH)
            throw new FilePasswordIsToLongException();
        if (!newFilePassword.getPassword().equals(newFilePassword.getMatchPassword()))
            throw new FilePasswordsDoesNotMatchException();
    }

    /**
     * This method is used to process the '/file/change-password' PATCH rest endpoint requests and change the password of the requested file.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param changeFilePassword A ChangeFilePasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message' and, 'data' 'status' keys and the status code is 200.
     * @throws InvalidFileRequestException If the method arguments are null or the ChangeFilePasswordDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws FileDoesNotHavePasswordException If the requested file is not protected by a password.
     * @throws InvalidOriginalPasswordException If the original file password provided is not the same as the current file password.
     * @throws InterruptedFileRequestException If the file repository does not apply the requested changes.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     * @throws FilePasswordsDoesNotMatchException If the new password and match new password are not equals.
     */
    @Override
    public ResponseEntity<Object> changeFilePassword(final Authentication authentication, final ChangeFilePasswordDto changeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidOriginalPasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            FilePasswordsDoesNotMatchException,
            FileDoesNotHavePasswordException {
        this.validateChangeFilePasswordRequest(authentication, changeFilePassword);
        UUID fileId = changeFilePassword.getFileId();
        try {
            String encodedPassword = this.passwordEncoder().encode(changeFilePassword.getNewPassword());
            this.fileRepository.changeFilePassword(fileId, encodedPassword);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFileRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.change-file-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateChangeFilePasswordRequest(final Authentication authentication, final ChangeFilePasswordDto changeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            FilePasswordsDoesNotMatchException,
            InvalidOriginalPasswordException,
            FileDoesNotHavePasswordException {
        if (authentication == null || changeFilePassword == null || !changeFilePassword.isValid())
            throw new InvalidFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = changeFilePassword.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        boolean havePassword = this.fileRepository.fileHavePassword(fileId);
        if (havePassword) {
            if (changeFilePassword.getOriginalPassword().length() < MIN_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToShortException();
            if (changeFilePassword.getOriginalPassword().length() > MAX_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToLongException();
            if (changeFilePassword.getNewPassword().length() < MIN_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToShortException();
            if (changeFilePassword.getNewPassword().length() > MAX_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToLongException();
            if (!changeFilePassword.getNewPassword().equals(changeFilePassword.getMatchNewPassword()))
                throw new FilePasswordsDoesNotMatchException();
            try {
                boolean validOriginalPassword = this.fileRepository.filePasswordMatch(fileId, changeFilePassword.getOriginalPassword());
                if (!validOriginalPassword)
                    throw new InvalidOriginalPasswordException();
            } catch (FileNotFoundException e) {
                throw new FileNotExistsException();
            }
        } else throw new FileDoesNotHavePasswordException();
    }

    /**
     * This method is used to process the '/file/remove-password' PATCH rest endpoint requests and remove the current password of the requested file.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param removeFilePassword A RemoveFilePasswordDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws InvalidFileRequestException If the method arguments are null or the RemoveFilePasswordDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileDoesNotHavePasswordException If the requested file is not protected by a password.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws InvalidFilePasswordException If the file password provided is not the same as the current file password.
     * @throws InterruptedFileRequestException If the file repository does not apply the requested changes.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> removeFilePassword(final Authentication authentication, final RemoveFilePasswordDto removeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            InterruptedFileRequestException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException,
            FileDoesNotHavePasswordException {
        this.validateRemoveFilePassword(authentication, removeFilePassword);
        UUID fileId = removeFilePassword.getFileId();
        try {
            this.fileRepository.removeFilePassword(fileId);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        } catch (UnfinishedRepositoryOperationException e) {
            throw new InterruptedFileRequestException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.remove-file-password-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateRemoveFilePassword(final Authentication authentication, final RemoveFilePasswordDto removeFilePassword)
            throws InvalidFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            InvalidFilePasswordException,
            FileDoesNotHavePasswordException {
        if (authentication == null || removeFilePassword == null || !removeFilePassword.isValid())
            throw new InvalidFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = removeFilePassword.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        boolean havePassword = this.fileRepository.fileHavePassword(fileId);
        if (havePassword) {
            if (removeFilePassword.getPassword().length() < MIN_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToShortException();
            if (removeFilePassword.getPassword().length() > MAX_FILE_PASSWORD_LENGTH)
                throw new FilePasswordIsToLongException();
            try {
                boolean validOriginalPassword = this.fileRepository.filePasswordMatch(fileId, removeFilePassword.getPassword());
                if (!validOriginalPassword)
                    throw new InvalidFilePasswordException();
            } catch (FileNotFoundException e) {
                throw new FileNotExistsException();
            }
        } else throw new FileDoesNotHavePasswordException();
    }

    /**
     * This method is used to process the '/file/remove-password' PATCH rest endpoint requests and deletes the requested file.
     * @param authentication An Authentication interface instance that represents the request authenticated principal.
     * @param fileRequest A FileRequestDto class instance that represents the request body data.
     * @return A ResponseEntity class instance that represents the request response.
     * The ResponseEntity content type is 'APPLICATION_JSON', the body is a map containing the 'message', 'data' and 'status' keys and the status code is 200.
     * @throws InvalidDeleteFileRequestException If the method arguments are null or the FileRequestDto instance is not valid.
     * @throws UnauthenticatedUserException If the request principal is not authenticated or null.
     * @throws FileNotExistsException If the requested file does not exist.
     * @throws ForbiddenFileAccessException If the current user is not the owner of the requested file.
     * @throws InvalidFilePasswordException If the file password provided is not the same as the current file password.
     * @throws FilePasswordIsToShortException If the file password length is lower than the min length established by the MIN_FILE_PASSWORD_LENGTH class property.
     * @throws FilePasswordIsToLongException If the file password length is greater than the max length established by the MAX_FOLDER_PASSWORD_LENGTH class property.
     */
    @Override
    public ResponseEntity<Object> deleteFile(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidDeleteFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            InvalidFilePasswordException,
            FilePasswordIsToLongException,
            FilePasswordIsToShortException {
        this.validateDeleteFileRequest(authentication, fileRequest);
        UUID fileId = fileRequest.getFileId();
        try {
            this.fileRepository.deleteFile(fileId);
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        }
        String successMessage = this.getLocalizedMessage("lang.message.remove-file-success");
        return RestResponse.build(successMessage, HttpStatus.OK);
    }

    private void validateDeleteFileRequest(final Authentication authentication, final FileRequestDto fileRequest)
            throws InvalidDeleteFileRequestException,
            UnauthenticatedUserException,
            FileNotExistsException,
            ForbiddenFileAccessException,
            FilePasswordIsToShortException,
            FilePasswordIsToLongException,
            InvalidFilePasswordException {
        if (authentication == null || fileRequest == null || !fileRequest.isValid())
            throw new InvalidDeleteFileRequestException();
        this.validateRestAuthentication(authentication);
        UUID fileId = fileRequest.getFileId();
        boolean fileExists = this.fileRepository.fileExists(fileId);
        if (!fileExists)
            throw new FileNotExistsException();
        String userEmail = this.getRestUserEmailAddress(authentication);
        boolean userIsOwner = this.fileRepository.userIsOwner(userEmail, fileId);
        if (!userIsOwner)
            throw new ForbiddenFileAccessException();
        try {
            boolean fileHavePassword = this.fileRepository.fileHavePassword(fileId);
            if (fileHavePassword) {
                if (fileRequest.getFilePassword().length() < MIN_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToShortException();
                if (fileRequest.getFilePassword().length() > MAX_FILE_PASSWORD_LENGTH)
                    throw new FilePasswordIsToLongException();
                boolean validOriginalPassword = this.fileRepository.filePasswordMatch(fileId, fileRequest.getFilePassword());
                if (!validOriginalPassword)
                    throw new InvalidFilePasswordException();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotExistsException();
        }
    }
}
