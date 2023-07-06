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
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("FileRestController GetFile Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT20S")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileRestControllerGetFileEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private UUID createdFileId;

    private static final String getFilePath = "/file/get-file";

    @BeforeAll
    public void setup() throws JsonProcessingException {
        //given
        String loginPath = "/login";
        String addFilePath = "/file/add-file";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String urlEncodedLoginForm = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
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
        this.tokenCookie = webTestClient.post()
                .uri(loginPath)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodedLoginForm)
                .exchange()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        assertNotNull(tokenCookie);
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFilePath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(inserter)
                .exchange()
                .returnResult(String.class);
        //then
        String body = response.getResponseBody().blockFirst();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(200, jsonResponse.getStatus());
        assertEquals("File upload successfully", jsonResponse.getMessage());
        assertNotNull(jsonResponse.getData());
        this.createdFileId = UUID.fromString(jsonResponse.getData());
    }

    @AfterAll
    public void teardown() throws JsonProcessingException {
        //given
        String logoutPath = "/logout";
        String deleteFilePath = "/file/delete";
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "123456", "text_to_test.txt");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(deleteFilePath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(fileRequestDto))
                .exchange()
                .returnResult(String.class);
        String body = response.getResponseBody().blockFirst();
        assertEquals(HttpStatus.OK, response.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(200, jsonResponse.getStatus());
        assertEquals("File successfully deleted", jsonResponse.getMessage());
        response = webTestClient.post()
                .uri(logoutPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @Order(1)
    @DisplayName("'/file/get-file' does not throw exception and status 200 | ")
    public void getFileResourceDoesNotThrowExceptionAndAssertStatus200Test() throws IOException {
        //given
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "123456", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        //when
        FluxExchangeResult<byte[]> response = webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
                .exchange()
                .returnResult(byte[].class);
        assertEquals(HttpStatus.OK, response.getStatus());
        byte[] result = response.getResponseBody().blockFirst();
        byte[] originalFile = Files.readString(Paths.get("src/test/resources/file_to_test.txt")).getBytes(StandardCharsets.UTF_8);
        assertNotNull(result);
        assertNotNull(originalFile);
        assertEquals(result.length, originalFile.length);
        String hexResult = new String(Hex.encode(result));
        String hexOriginalFile = new String(Hex.encode(originalFile));
        assertEquals(hexResult, hexOriginalFile);
    }

    @Test
    @Order(2)
    @DisplayName("'/file/get-file' status 400 | File not exists")
    public void getFileStatus400FileNotExistsTest() throws JsonProcessingException {
        //given
        FileRequestDto fileRequestDto = new FileRequestDto(UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6"), "123456", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        //when
        FluxExchangeResult<String> response = webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
                .exchange()
                .returnResult(String.class);
        //the
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("File does not exist", jsonResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("'/file/get-file' status 400 | File password is to short")
    public void getFileStatus400FilePasswordIsToShortTest() throws JsonProcessingException {
        //given
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "123", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        //when
        FluxExchangeResult<String> response = this.webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
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
    @Order(4)
    @DisplayName("'/file/get-file' status 400 | File password is to long")
    public void getFileStatus400FilePasswordIsToLongTest() throws JsonProcessingException {
        //given
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "012345678901234567890123456789012345678901234567891", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        //when
        FluxExchangeResult<String> response = this.webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
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
    @Order(5)
    @DisplayName("'/file/get-file' status 400 | Invalid file password")
    public void getFileStatus400InvalidFilePasswordTest() throws JsonProcessingException {
        //given
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "654321", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        //when
        FluxExchangeResult<String> response = this.webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Invalid file password", jsonResponse.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("'/file/get-file' status 400 | Forbidden file access")
    public void getFileStatus400ForbiddenFileAccessTest() throws JsonProcessingException {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("user@user.com", "user");
        String urlEncodedLoginForm = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        //when
        FluxExchangeResult<String> response = webTestClient.post()
                .uri("/logout")
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.FOUND, response.getStatus());
        this.tokenCookie = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodedLoginForm)
                .exchange()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        assertNotNull(tokenCookie);
        FileRequestDto fileRequestDto = new FileRequestDto(this.createdFileId, "123456", "file_to_test.txt");
        String urlEncodingForm = URLEncodedFormTool.objectToEncodedFormString(fileRequestDto);
        response = this.webTestClient.post()
                .uri(getFilePath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingForm)
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("You don't have access to this file", jsonResponse.getMessage());
        response = webTestClient.post()
                .uri("/logout")
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.FOUND, response.getStatus());
        loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        urlEncodedLoginForm = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        this.tokenCookie = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodedLoginForm)
                .exchange()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        assertNotNull(tokenCookie);
    }
}
