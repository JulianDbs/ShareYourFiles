package com.juliandbs.shareyourfiles.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.juliandbs.shareyourfiles.persistence.user.dto.DeleteAccountRequestDto;
import com.juliandbs.shareyourfiles.tools.JsonResponse;

import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.htmlunit.util.Cookie;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("UserController View Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerViewTest {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int serverPort;

    private WebClient webClient;

    private Cookie tokenCookie;

    @BeforeEach
    public void init() {
        this.webClient = new WebClient();
    }

    @AfterAll
    public void teardown() throws JsonProcessingException {
        //given
        DeleteAccountRequestDto deleteAccountRequestDto = new DeleteAccountRequestDto("123456", "123456");
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
    @DisplayName("'/login' GET endpoint Login view test")
    public void loginViewDoesNotThrowExceptionAndAssertContentTest() {
        assertDoesNotThrow( () -> {
         HtmlPage loginPage = this.webClient.getPage("http://localhost:" + serverPort + "/login");
         assertNotNull(loginPage);
         assertEquals("UTF-8", loginPage.getCharset().displayName());
         assertEquals("Login", loginPage.getTitleText());
         HtmlForm loginForm = (HtmlForm)loginPage.getElementById("login-form");
         assertNotNull(loginForm);
         HtmlInput emailInput = loginForm.getInputByName("email");
         assertNotNull(emailInput);
         emailInput.setValue("admin@admin.com");
         HtmlInput passwordInput = loginForm.getInputByName("password");
         assertNotNull(passwordInput);
         passwordInput.setValue("admin");
         HtmlButton loginButton = (HtmlButton)loginPage.getElementById("login-form-button");
         assertNotNull(loginButton);
         HtmlPage desktopPage = loginButton.click();
         assertEquals("Desktop", desktopPage.getTitleText());
         HtmlButton logoutButton = (HtmlButton)desktopPage.getElementById("logout-form-button");
         assertNotNull(logoutButton);
         HtmlPage homePage = logoutButton.click();
         assertEquals("Home", homePage.getTitleText());
        });
    }

    @Test
    @Order(2)
    @DisplayName("'/registration' GET endpoint Registration view test")
    public void registrationViewDoesNotThrowExceptionAndAssertContentTest() {
        String newUserName = "testuser";
        String newUserEmail = "testuser@testuser.com";
        String newUserPassword = "123456";
        assertDoesNotThrow( () -> {
            HtmlPage registrationPage = this.webClient.getPage("http://localhost:" + serverPort + "/registration");
            assertNotNull(registrationPage);
            assertEquals("UTF-8", registrationPage.getCharset().displayName());
            assertEquals("Registration", registrationPage.getTitleText());
            HtmlForm registrationForm = (HtmlForm)registrationPage.getElementById("registration-form");
            assertNotNull(registrationForm);
            HtmlInput usernameInput = registrationForm.getInputByName("username");
            assertNotNull(usernameInput);
            HtmlInput emailInput = registrationForm.getInputByName("email");
            assertNotNull(emailInput);
            HtmlInput passwordInput = (HtmlInput)registrationPage.getElementById("password");
            assertNotNull(passwordInput);
            HtmlInput matchPasswordInput = (HtmlInput)registrationPage.getElementById("matchPassword");
            assertNotNull(matchPasswordInput);
            usernameInput.setValue(newUserName);
            emailInput.setValue(newUserEmail);
            passwordInput.setValue(newUserPassword);
            matchPasswordInput.setValue(newUserPassword);
            HtmlButton registrationButton = (HtmlButton)registrationPage.getElementById("registration-form-button");
            assertNotNull(registrationButton);
            HtmlPage registrationSuccessfullyPage = registrationButton.click();
            assertNotNull(registrationSuccessfullyPage);
            assertEquals("Registration Success", registrationSuccessfullyPage.getTitleText());
            HtmlAnchor singInAnchor = registrationSuccessfullyPage.getHtmlElementById("registration-success-button");
            assertNotNull(singInAnchor);
            HtmlPage loginPage = singInAnchor.click();
            assertNotNull(loginPage);
            assertEquals("Login", loginPage.getTitleText());
            HtmlForm loginForm = (HtmlForm)loginPage.getElementById("login-form");
            assertNotNull(loginForm);
            HtmlInput loginEmailInput = (HtmlInput)loginPage.getElementById("email");
            assertNotNull(loginEmailInput);
            loginEmailInput.setValue(newUserEmail);
            HtmlInput loginPasswordInput = (HtmlInput)loginPage.getElementById("password");
            assertNotNull(loginPasswordInput);
            loginPasswordInput.setValue(newUserPassword);
            HtmlButton loginButton = (HtmlButton)loginPage.getElementById("login-form-button");
            assertNotNull(loginButton);
            HtmlPage desktopPage = loginButton.click();
            assertEquals("Desktop", desktopPage.getTitleText());
            Optional<Cookie> optional = this.webClient.getCookieManager().getCookies().stream().filter(c -> c.getName().equals("token")).findFirst();
            assertTrue(optional.isPresent());
            this.tokenCookie = optional.get();
        });
    }
}
