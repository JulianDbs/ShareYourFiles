package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.user.dto.DeleteAccountRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.persistence.user.dto.UserDto;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("UserRestController DeleteAccount Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRestControllerDeleteAccountTest {

    @LocalServerPort
    private int randomServerPort;

    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private static final String deleteAccountPath = "/user/delete-account";

    @BeforeAll
    public void setup() {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + randomServerPort).defaultHeader("Accept-Language", "en").build();
        //given
        UserDto userDto = new UserDto("test", "test@test.com", "123456", "123456");
        String urlEncodingFormRegistration = URLEncodedFormTool.objectToEncodedFormString(userDto);
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@test.com", "123456");
        String urlEncodingFormLogin = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        //when
        this.webTestClient.post()
                .uri("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingFormRegistration)
                .exchange()
                .returnResult(FluxExchangeResult.class).getStatus().isSameCodeAs(HttpStatus.OK);
        this.tokenCookie = webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(urlEncodingFormLogin)
                .exchange()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        assertNotNull(this.tokenCookie);
    }

    @AfterAll
    public void teardown() throws JsonProcessingException {
        webTestClient.post()
                .uri("/logout")
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isFound();
    }

    @Test
    @Order(1)
    @DisplayName("'/user/delete-account' status 400 | User password is to short")
    public void deleteAccountStatus400UserPasswordIsToShortTest() throws JsonProcessingException {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("123", "123");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(deleteAccountPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deleteAccountRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("User password have lest than 6 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("'/user/delete-account' status 400 | User password is to long")
    public void deleteAccountStatus400UserPasswordIsToLongTest() throws JsonProcessingException {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("012345678901234567890123456789012345678901234567891", "012345678901234567890123456789012345678901234567891");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(deleteAccountPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deleteAccountRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("User password have more than 50 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("'/user/delete-account' status 400 | Passwords does not Match")
    public void deleteAccountStatus400PasswordsDoesNotMatchTest() throws JsonProcessingException {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("123456", "654321");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(deleteAccountPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deleteAccountRequestDto))
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
    @Order(4)
    @DisplayName("'/user/delete-account' status 400 | Invalid user password")
    public void deleteAccountStatus400InvalidUserPasswordTest() throws JsonProcessingException {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("654321", "654321");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(deleteAccountPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(deleteAccountRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("Wrong user password", jsonResponse.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("'/user/delete-account' does not throw exception and status 200")
    public void deleteAccountDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("123456", "123456");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = this.webTestClient.patch()
                    .uri(deleteAccountPath)
                    .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(deleteAccountRequestDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("Account deleted successfully", jsonResponse.getMessage());
        });
    }
}
