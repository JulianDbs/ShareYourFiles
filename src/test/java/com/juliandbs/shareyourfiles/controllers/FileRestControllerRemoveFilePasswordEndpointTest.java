package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.file.dto.FileRequestDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.NewFilePasswordDto;
import com.juliandbs.shareyourfiles.persistence.file.dto.RemoveFilePasswordDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.tools.JsonResponse;
import com.juliandbs.shareyourfiles.tools.URLEncodedFormTool;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("FileRestController RemoveFilePassword Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileRestControllerRemoveFilePasswordEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private UUID createdFileId;

    private static final String removePasswordPath = "/file/remove-password";

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
    @DisplayName("'/file/remove-password' status 400 | File does not exists")
    public void removePasswordStatus400FileDoesNotExistsTest() throws JsonProcessingException {
        //given
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6"), "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("File does not exist", jsonResponse.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("'/file/remove-password' status 400 | File password is to short")
    public void removePasswordStatus400FilePasswordIsToShortTest() throws JsonProcessingException {
        //given
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "123");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
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
    @Order(3)
    @DisplayName("'/file/remove-password' status 400 | File password is to long")
    public void removePasswordStatus400FilePasswordIsToLongTest() throws JsonProcessingException {
        //given
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "012345678901234567890123456789012345678901234567892");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
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
    @Order(4)
    @DisplayName("'/file/remove-password' status 400 | Invalid file password")
    public void removePasswordStatus400InvalidFilePasswordTest() throws JsonProcessingException {
        //given
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "654321");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
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
    @Order(5)
    @DisplayName("'/file/set-password' status 400 | Forbidden file access")
    public void setPasswordStatus400ForbiddenFileAccessTest() throws JsonProcessingException {
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
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "123456");
        response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
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

    @Test
    @Order(6)
    @DisplayName("/file/remove-password' does not throw exception and status 200 | File password removed successfully")
    public void removePasswordDoesNotThrowExceptionAndAssertStatus200Test() {
        //give
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "123456");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = this.webTestClient.patch()
                    .uri(removePasswordPath)
                    .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(removeFilePasswordDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("File password removed successfully", jsonResponse.getMessage());
        });
    }

    @Test
    @Order(7)
    @DisplayName("'/file/remove-password' status 400 | File does not have a password")
    public void removePasswordStatus400FileDoesNotHavePasswordTest() throws JsonProcessingException {
        //given
        RemoveFilePasswordDto removeFilePasswordDto = new RemoveFilePasswordDto(this.createdFileId, "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(removePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeFilePasswordDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("The file does not have a password", jsonResponse.getMessage());
    }
}
