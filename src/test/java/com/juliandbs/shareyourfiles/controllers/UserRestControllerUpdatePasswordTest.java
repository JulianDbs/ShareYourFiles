package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliandbs.shareyourfiles.persistence.user.dto.*;
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
@DisplayName("UserRestController UpdatePassword Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRestControllerUpdatePasswordTest {

    @LocalServerPort
    private int randomServerPort;

    private WebTestClient webTestClient;

    private ResponseCookie tokenCookie;

    private static final String changePasswordPath = "/user/change-password";

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
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("654321", "654321");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri("/user/delete-account")
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
        webTestClient.post()
                .uri("/logout")
                .cookies( cookies -> cookies.add(tokenCookie.getName(), tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isFound();
    }

    @Test
    @Order(1)
    @DisplayName("'/user/change-password' status 400 | New password is the same")
    public void updatePasswordStatus400NewPasswordIsTheSameTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123456", "123456", "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("New user password is the same as the original", jsonResponse.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("'/user/change-password' status 400 | User password it to short")
    public void updatePasswordStatus400UserPasswordIsToShortTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123", "123456", "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
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
    @Order(3)
    @DisplayName("'/user/change-password' status 400 | User password it to long")
    public void updatePasswordStatus400UserPasswordIsToLongTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("012345678901234567890123456789012345678901234567891", "123456", "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
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
    @Order(4)
    @DisplayName("'/user/change-password' status 400 | User new password it to short")
    public void updatePasswordStatus400UserNewPasswordIsToShortTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123456", "123", "123");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("New user password have lest than 6 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("'/user/change-password' status 400 | User new password it to long")
    public void updatePasswordStatus400UserNewPasswordIsToLongTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123456", "012345678901234567890123456789012345678901234567891", "012345678901234567890123456789012345678901234567891");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
                .exchange()
                .returnResult(String.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        String body = response.getResponseBody().blockFirst();
        assertNotNull(body);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
        assertEquals(400, jsonResponse.getStatus());
        assertEquals("New user password have more than 50 characters", jsonResponse.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("'/user/change-password' status 400 | Password does not match")
    public void updatePasswordStatus400PasswordDoesNotMatchTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123456", "654321", "123456");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
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
    @DisplayName("'/user/change-password' status 400 | Invalid user password")
    public void updatePasswordStatus400InvalidUserPasswordTest() throws JsonProcessingException {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("654321", "654123", "654123");
        //when
        FluxExchangeResult<String> response = this.webTestClient.patch()
                .uri(changePasswordPath)
                .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
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
    @Order(8)
    @DisplayName("'/user/change-password' does not throw exception and status 200 | Password updated successfully")
    public void updatePasswordDoesNotThrowExceptionAndAssertStatus200Test() {
        //given
        ChangeAccountPasswordRequestDto changeAccountPasswordRequestDto = new ChangeAccountPasswordRequestDto("123456", "654321", "654321");
        //when
        assertDoesNotThrow( () -> {
            FluxExchangeResult<String> response = this.webTestClient.patch()
                    .uri(changePasswordPath)
                    .cookies(cookies -> cookies.add(this.tokenCookie.getName(), this.tokenCookie.getValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(changeAccountPasswordRequestDto))
                    .exchange()
                    .returnResult(String.class);
            //then
            assertEquals(HttpStatus.OK, response.getStatus());
            String body = response.getResponseBody().blockFirst();
            assertNotNull(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(body, JsonResponse.class);
            assertEquals(200, jsonResponse.getStatus());
            assertEquals("Password updated successfully", jsonResponse.getMessage());
        });
    }
}
