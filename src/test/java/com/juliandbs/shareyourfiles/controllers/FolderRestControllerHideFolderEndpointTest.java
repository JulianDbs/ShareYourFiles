package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.folder.dto.DeleteFolderDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.FolderRequestDto;
import com.juliandbs.shareyourfiles.persistence.folder.dto.NewFolderDto;
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
@DisplayName("FolderRestController HideFolder Endpoint Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FolderRestControllerHideFolderEndpointTest {

    @LocalServerPort
    private int randomServerPort;

    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private UUID createdFolderId;

    private static final String hideFolderPath = "/folder/hide-folder";

    @BeforeEach
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

    @AfterEach
    public void teardown() {
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
    @DisplayName("'/folder/hide-folder' does not throw exception and status is 200 | Folder is now private")
    public void hideFolderDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(this.createdFolderId, "123456");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = webTestClient.patch()
                    .uri(hideFolderPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                    .body(BodyInserters.fromValue(folderRequestDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("Folder is now private", jsonResponse.getMessage());
        });
    }

    @Test
    @Order(2)
    @DisplayName("'/folder/hide-folder' status 400 | Folder does not exists")
    public void hideFolderStatus400FolderDoesNotExistsTest() throws JsonProcessingException {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(UUID.fromString("123e36b3-1f65-11ed-a4f5-525506c439f6"), "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(hideFolderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .body(BodyInserters.fromValue(folderRequestDto))
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
    @Order(3)
    @DisplayName("'/folder/hide-folder' status 400 | Folder password is to short")
    public void hideFolderStatus400FolderPasswordIsToShortTest() throws JsonProcessingException {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(this.createdFolderId, "123");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(hideFolderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), tokenCookie.getValue()))
                .body(BodyInserters.fromValue(folderRequestDto))
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
    @Order(4)
    @DisplayName("'/folder/hide-folder' status 400 | Folder password is to long")
    public void hideFolderStatus400FolderPasswordIsToLongTest() throws JsonProcessingException {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(this.createdFolderId, "012345678901234567890123456789012345678901234567891");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(hideFolderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), tokenCookie.getValue()))
                .body(BodyInserters.fromValue(folderRequestDto))
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
    @Order(5)
    @DisplayName("'/folder/hide-folder' status 400 | Invalid folder password")
    public void hideFolderStatus400InvalidFolderPasswordTest() throws JsonProcessingException {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(this.createdFolderId, "654321");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(hideFolderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), tokenCookie.getValue()))
                .body(BodyInserters.fromValue(folderRequestDto))
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
    @Order(6)
    @DisplayName("'/folder/hide-folder' status 400 | Forbidden folder access" )
    public void hideFolderStatus400ForbiddenFolderAccessTest() throws JsonProcessingException {
        //given
        FolderRequestDto folderRequestDto = new FolderRequestDto(UUID.fromString("a42d43f4-f949-11ed-b69a-525400c439f6"), "123456");
        //when
        FluxExchangeResult<String> response = webTestClient.patch()
                .uri(hideFolderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .body(BodyInserters.fromValue(folderRequestDto))
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
}
