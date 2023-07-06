package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.tools.JsonResponse;
import com.juliandbs.shareyourfiles.tools.URLEncodedFormTool;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("FileRestController AddNewFile Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FileRestControllerAddNewFileEndpointTest {

    @LocalServerPort
    private int randomServerPort;


    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private static final String addFilePath = "/file/add-file";

    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + randomServerPort).defaultHeader("Accept-Language", "en").build();
        //given
        String loginPath = "/login";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String urlEncodedLoginForm = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        //when
        this.tokenCookie = webTestClient.post()
                .uri(loginPath)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodedLoginForm)
                .exchange()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        //then
        assertNotNull(tokenCookie);
    }

    @AfterEach
    public void teardown() {
        String logoutPath = "/logout";
        FluxExchangeResult<String> response = webTestClient.post()
                .uri(logoutPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @Order(1)
    @DisplayName("'/file/add-file' and '/file/delete' does not throw exception and status 200 | File upload successfully")
    public void addNewFileAndDeleteFileDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        String deleteFilePath = "/file/delete";
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "123456");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = webTestClient.put()
                    .uri(addFilePath)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(inserter)
                    .exchange()
                    .returnResult(String.class);
            String body = response.getResponseBody().blockFirst();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("File upload successfully", jsonResponse.getMessage());
            assertNotNull(jsonResponse.getData());
            UUID createdFileId = UUID.fromString(jsonResponse.getData());
            FileRequestDto fileRequestDto = new FileRequestDto(createdFileId, "123456", "text_to_test.txt");
            response = webTestClient.patch()
                    .uri(deleteFilePath)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(fileRequestDto))
                    .exchange()
                    .returnResult(String.class);
            body = response.getResponseBody().blockFirst();
            assertEquals(HttpStatus.OK, response.getStatus());
            objectMapper = new ObjectMapper();
            jsonResponse = objectMapper.readValue(body , JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("File successfully deleted", jsonResponse.getMessage());
        });
    }

    @Test
    @Order(2)
    @DisplayName("'/file/add-file' status 400 | New file is to big")
    public void addNewFileStatus400NewFileIsToBigTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/big_file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "123456");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("File size is over the limit", jsonResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("'/file/add-file' status 400 | Folder does not exists")
    public void addNewFileStatus400FolderDoesNotExistsTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.getPath()));
        multiValueMap.add("folderId", "dafe36ba-ef65-11ed-a5f5-123400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "123456");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Folder does not exists", jsonResponse.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("'/file/add-file' status 400 | File password is to short")
    public void addNewFileStatus400FilePasswordIsToShortTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123");
        multiValueMap.add("matchPassword", "123");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("File password have lest than 6 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("'/file/add-file' status 400 | File password is to long")
    public void addNewFileStatus400FilePasswordIsToLongTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "012345678901234567890123456789012345678901234567891");
        multiValueMap.add("matchPassword", "012345678901234567890123456789012345678901234567891");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("File password have more than 50 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("'/file/add-file' status 400 | File password does not match")
    public void addNewFileStatus400FilePasswordDoesNotMatchTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "654321");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Passwords does not match", jsonResponse.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("'/file/add-file' status 400 | Forbidden folder access")
    public void addNewFileStatus400ForbiddenFolderAccessTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.toPath()));
        multiValueMap.add("folderId", "a42d43f4-f949-11ed-b69a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "123456");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("You don't have access to this folder", jsonResponse.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("'/file/add-file' status 400 | File is not valid")
    public void addNewFileStatus400FileIsNotValidTest() throws JsonProcessingException {
        //given
        File file = new File("src/test/resources/empty_file_to_test.txt");
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("fromUrl", "/desktop");
        multiValueMap.add("file", new FileSystemResource(file.getPath()));
        multiValueMap.add("folderId", "711de2dc-eaa0-11ed-af9a-525400c439f6");
        multiValueMap.add("fileIsPrivate", true);
        multiValueMap.add("fileHavePassword", true);
        multiValueMap.add("password", "123456");
        multiValueMap.add("matchPassword", "123456");
        BodyInserters.FormInserter<Object> inserter = BodyInserters.fromMultipartData(multiValueMap);
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Empty new file", jsonResponse.getMessage());
    }
}
