package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.folder.dto.DeleteFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderNameDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.tools.JsonResponse;
import com.juliandbs.shareyourfiles.tools.URLEncodedFormTool;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("FolderRestController UpdateFolderName Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FolderRestControllerChangeFolderNameEndpointTest {

    @LocalServerPort
    private int randomServerPort;

    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private UUID createdFolderId;

    private static final String updateFolderNamePath = "/folder/change-folder-name";

    @BeforeAll
    public void init() throws JsonProcessingException {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + randomServerPort).defaultHeader("Accept-Language", "en").build();
        //given
        String loginPath = "/login";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String urlEncodedLoginForm = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "private_folder", true, true, "123456", "123456");
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
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(200, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getData());
        this.createdFolderId = UUID.fromString(jsonResponse.getData());
    }

    @AfterAll
    public void teardown() {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + randomServerPort).defaultHeader("Accept-Language", "en").build();
        String deleteFolderPath = "/folder/delete";
        String logoutPath = "/logout";
        DeleteFolderDto deleteFolderDto = new DeleteFolderDto(this.createdFolderId, "123456");
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(deleteFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deleteFolderDto))
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        response = webTestClient.post()
                .uri(logoutPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);
        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @Order(1)
    @DisplayName("'/folder/change-folder-name' status 400 | Folder does not exists")
    public void changeFolderNameStatus400FolderDoesNotExistsTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(UUID.fromString("dafe36ba-ef65-11ed-a5f5-123400c439f6"), "private folder", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
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
    @Order(2)
    @DisplayName("'/folder/change-folder-name' status 400 | Same folder name")
    public void changeFolderNameStatus400SameFolderNameTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "private_folder", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("New folder name is the same as the original", jsonResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("'/folder/change-folder-name' status 400 | Folder name is to short")
    public void changeFolderNameStatus400FolderNameIsToShortTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "abc", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Folder name have lest than 4 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("'/folder/change-folder-name' status 400 | Folder name is to long")
    public void changeFolderNameStatus400FolderNameIsToLongTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "abcdefghijabcdefghijabcdefghijabcdefghija", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Folder name have more than 40 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("'/folder/change-folder-name' status 400 | Folder password is to short")
    public void changeFolderNameStatus400FolderPasswordIsToShortTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "private folder", "123");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Folder password have lest than 6 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("'/folder/change-folder-name' status 400 | Folder password is to long")
    public void changeFolderNameStatus400FolderPasswordIsToLongTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "private folder", "012345678901234567890123456789012345678901234567891");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Folder password have more than 50 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("'/folder/change-folder-name' status 400 | Invalid folder password")
    public void changeFolderNameStatus400InvalidFolderPasswordTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "private folder", "654321");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Invalid folder password", jsonResponse.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("'/folder/change-folder-name' status 400 | Forbidden folder access")
    public void changeFolderNameStatus400ForbiddenFolderAccessTest() throws JsonProcessingException {
        //give
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(UUID.fromString("a42d43f4-f949-11ed-b69a-525400c439f6"), "private folder", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(updateFolderNamePath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(newFolderNameDto))
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
    @Order(9)
    @DisplayName("'/folder/change-folder-name' does not throw exception and status is 200 | Folder name updated successfully")
    public void changeFolderNameDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        NewFolderNameDto newFolderNameDto = new NewFolderNameDto(this.createdFolderId, "private folder", "123456");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = webTestClient.patch()
                    .uri(updateFolderNamePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .body(BodyInserters.fromValue(newFolderNameDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("Folder name updated successfully", jsonResponse.getMessage());
        });
    }
}
