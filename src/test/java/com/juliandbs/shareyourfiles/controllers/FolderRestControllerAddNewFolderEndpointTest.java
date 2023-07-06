package com.juliandbs.shareyourfiles.controllers;

import com.juliandbs.shareyourfiles.tools.JsonResponse;
import com.juliandbs.shareyourfiles.persistence.folder.dto.DeleteFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.tools.URLEncodedFormTool;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("FolderRestController AddNewFolder Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FolderRestControllerAddNewFolderEndpointTest {

    @LocalServerPort
    private int randomServerPort;

    private WebTestClient webTestClient;

    @Autowired
    private FolderRestController folderRestController;

    private ResponseCookie tokenCookie;

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
        //given
        String logoutPath = "/logout";
        //when
        FluxExchangeResult<String> response = webTestClient.post()
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
    @DisplayName("'/folder/add-folder' and '/folder/delete' does not throw exception and status is 200")
    public void addNewFolderAndDeleteFolderDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, false, "123", "123");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = webTestClient.put()
                    .uri(addFolderPath)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(newFolderDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertNotNull(jsonResponse.getData());
            UUID folderId = UUID.fromString(jsonResponse.getData());

            String deleteFolderPath = "/folder/delete";
            DeleteFolderDto deleteFolderDto = new DeleteFolderDto(folderId, "");
            response = webTestClient.patch()
                    .uri(deleteFolderPath)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(deleteFolderDto))
                    .exchange()
                    .returnResult(String.class);
            assertEquals(HttpStatus.OK, response.getStatus());
            body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            objectMapper = new ObjectMapper();
            jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("Folder deleted successfully", jsonResponse.getMessage());
        });
    }

    @Test
    @Order(2)
    @DisplayName("'/folder/add-folder' status is 400 | Folder name is to short")
    public void addNewFolderStatus400FolderNameIsToShortTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new", false, false, "123", "123");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Folder name have lest than 4 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("'/folder/add-folder' status is 400 | Folder name is to long")
    public void addNewFolderStatus400FolderNameIsToLongTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        String longFolderName = "01234567890123456789012345678901234567891";
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, longFolderName, false, false, "123", "123");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Folder name have more than 40 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("'/folder/add-folder' status is 400 | Folder password is to short")
    public void addNewFolderStatus400FolderPasswordIsToShortTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, true, "123", "123");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Folder password have lest than 6 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("'/folder/add-folder' status is 400 | Folder password is to long")
    public void addNewFolderStatus400FolderPasswordIsToLongTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        String longFolderPassword = "012345678901234567890123456789012345678901234567891";
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, true, longFolderPassword, "123");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Folder password have more than 50 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("'/folder/add-folder' status is 400 | Folder password does not match")
    public void addNewFolderStatus400FolderPasswordDoesNotMatchTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("711de2dc-eaa0-11ed-af9a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, true, "123456", "654321");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Folder passwords does not match", jsonResponse.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("'/folder/add-folder' status is 400 | Parent folder does not exists")
    public void addNewFolderStatus400ParentFolderDoesNotExistsTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("123e36ba-ef65-11ed-a5f5-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, false, "123456", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Parent folder does not exists", jsonResponse.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("'/folder/add-folder' status is 400 | Forbidden parent folder access")
    public void addNewFolderStatus400ForbiddenParentFolderAccessTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        UUID rootFolderId = UUID.fromString("a42d43f4-f949-11ed-b69a-525400c439f6");
        NewFolderDto newFolderDto = new NewFolderDto(rootFolderId, "new_folder", false, false, "123456", "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("You don't have access to the parent folder", jsonResponse.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("'/folder/add-folder' status is 400 | Http message not readable")
    public void addNewFolderStatus400HttpMessageNotReadableTest() throws JsonProcessingException {
        //given
        String addFolderPath = "/folder/add-folder";
        NewFolderDto newFolderDto = new NewFolderDto();
        //when
        FluxExchangeResult<String> response = webTestClient.put()
                .uri(addFolderPath)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newFolderDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body , JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getMessage());
        assertEquals("Invalid json request body", jsonResponse.getMessage());
    }
}
