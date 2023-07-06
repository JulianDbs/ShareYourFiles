package com.juliandbs.shareyourfiles.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Controllers ContextLoads Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllersContextLoadsTest {
    @Autowired
    private FileRestController fileRestController;

    @Autowired
    private FolderRestController folderRestController;

    @Autowired
    private UserRestController userRestController;

    @Autowired
    private FileController fileController;

    @Autowired
    private FolderController folderController;

    @Autowired
    private UserController userController;

    @Test
    @Order(1)
    @DisplayName("Controllers | contextLoads Test")
    public void contextLoads() {
        assertNotNull(fileRestController);
        assertNotNull(folderRestController);
        assertNotNull(userRestController);
        assertNotNull(fileController);
        assertNotNull(folderController);
        assertNotNull(userController);
    }

}

