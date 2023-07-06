package com.juliandbs.shareyourfiles;

import com.juliandbs.shareyourfiles.controllers.UserController;
import com.juliandbs.shareyourfiles.persistence.user.dto.LoginRequestDto;
import com.juliandbs.shareyourfiles.tools.URLEncodedFormTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */

@ExtendWith(SpringExtension.class)

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

@DisplayName("Login Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserController userController;

    @Test
    @Order(1)
    @DisplayName("Login Test | contextLoads")
    public void contextLoads() {
        assertNotNull(webTestClient);
        assertNotNull(userController);
    }

    @Test
    @Order(2)
    @DisplayName("Login Test | Login submit '/login' POST endpoint Test")
    public void loginSubmitTest() {
        String uri = "/login";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String body = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        //when
        ResponseCookie tokenCookie = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .exchange()
                .expectStatus().isFound()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        //then
        assertNotNull(tokenCookie);
        assertEquals(tokenCookie.getName(), "token");
        webTestClient.post()
                .uri("/logout")
                .cookies( cookie -> cookie.add(tokenCookie.getName(), tokenCookie.getValue()))
                .exchange()
                .expectStatus()
                .isFound();
    }

    @Test
    @Order(3)
    @DisplayName("Login Test | No logged '/desktop' GET endpoint Test")
    public void noLoggedDesktopTest() {
        webTestClient.get().uri("/login").exchange().expectStatus().isOk();
    }

    @Test
    @Order(4)
    @DisplayName("Login Test | logged in '/desktop' GET endpoint Test")
    public void loggedInDesktopTest() {
        //given
        String uri = "/login";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String body = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);

        //when
        ResponseCookie tokenCookie = webTestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .exchange()
                .expectStatus().isFound()
                .returnResult(FluxExchangeResult.class)
                .getResponseCookies()
                .getFirst("token");
        assertNotNull(tokenCookie);
        //then
        uri = "/desktop";
        webTestClient.get()
                .uri(uri)
                .cookies( cookie -> cookie.add(tokenCookie.getName(), tokenCookie.getValue()))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @Order(5)
    @DisplayName("LoginTest | Logout '/logout' POST endpoint Test")
    public void logoutTest() {
        //given
        String loginPath = "/login";
        String desktopPath = "/desktop";
        String logoutPath = "/logout";
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@admin.com", "admin");
        String body = URLEncodedFormTool.objectToEncodedFormString(loginRequestDto);
        ResponseCookie tokenCookie;
        //when
        FluxExchangeResult<WebTestClient.ResponseSpec> loginResponse = webTestClient.post()
                                                                                    .uri(loginPath)
                                                                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                                                    .bodyValue(body)
                                                                                    .exchange()
                                                                                    .expectStatus().isFound()
                                                                                    .returnResult(WebTestClient.ResponseSpec.class);
        assertNotNull(loginResponse);
        tokenCookie = loginResponse.getResponseCookies().getFirst("token");
        assertNotNull(tokenCookie);
        assertEquals(tokenCookie.getName(), "token");
        FluxExchangeResult<WebTestClient.ResponseSpec> logoutResponse = webTestClient.post()
                                                                                        .uri(logoutPath)
                                                                                        .cookies( cookie -> cookie.add(tokenCookie.getName(), tokenCookie.getValue()))
                                                                                        .exchange().returnResult(WebTestClient.ResponseSpec.class);
        //then
        assertNotNull(logoutResponse);
        assertEquals(logoutResponse.getStatus(), HttpStatus.FOUND);
        HttpHeaders logoutHeaders = logoutResponse.getResponseHeaders();
        assertNotNull(logoutHeaders);
        URI logoutLocation = logoutHeaders.getLocation();
        assertNotNull(logoutLocation);
        assertEquals(logoutLocation.getPath(), "/");
        FluxExchangeResult<WebTestClient.ResponseSpec> response =
        webTestClient.get()
                        .uri(desktopPath)
                        .cookies( cookie -> cookie.add(tokenCookie.getName(), tokenCookie.getValue()))
                        .exchange().returnResult(WebTestClient.ResponseSpec.class);
        assertNotNull(response);
        HttpHeaders headers = response.getResponseHeaders();
        assertNotNull(headers);
        URI location = headers.getLocation();
        assertNotNull(location);
        assertEquals(location.getPath(), "/login");
    }
}
