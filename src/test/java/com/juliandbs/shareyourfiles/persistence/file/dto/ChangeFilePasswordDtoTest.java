package com.juliandbs.shareyourfiles.persistence.file.dto;

import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author JulianDbs
 */
@DisplayName("Controllers ContextLoads Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChangeFilePasswordDtoTest {

    @Test
    @Order(1)
    @DisplayName("ChangeFilePasswordDto Constructors does not throw NullPointerException")
    public void constructorsDoesNotThrowNullPointerExceptionTest() {
        assertDoesNotThrow( () -> {
            ChangeFilePasswordDto instance1 = new ChangeFilePasswordDto(UUID.randomUUID(), "123456", "654321", "654321");
            ChangeFilePasswordDto instance2 = new ChangeFilePasswordDto(UUID.randomUUID());
        });
    }

    @Test
    @Order(2)
    @DisplayName("'ChangeFilePasswordDto Constructors throw NullPointerException")
    public void constructorsThrowNullPointerExceptionTest() {
        assertThrows( NullPointerException.class, () -> {
            ChangeFilePasswordDto instance1 = new ChangeFilePasswordDto(null, "123456", null, "654321");
            ChangeFilePasswordDto instance2 = new ChangeFilePasswordDto(null);
        });
    }

    @Test
    @Order(3)
    @DisplayName("ChangeFilePasswordDto Instance returns equals values")
    public void instanceReturnsEqualValuesTest() {
        UUID id = UUID.randomUUID();
        ChangeFilePasswordDto instance = new ChangeFilePasswordDto(id, "123456", "654321", "654123");
        assertEquals(id, instance.getFileId());
        assertEquals("123456", instance.getOriginalPassword());
        assertEquals("654321", instance.getNewPassword());
        assertEquals("654123", instance.getMatchNewPassword());
    }

    @Test
    @Order(4)
    @DisplayName("ChangeFilePasswordDto 'isValid' method returns true")
    public void assertIsValidReturnsTrueTest() {
        ChangeFilePasswordDto instance1 = new ChangeFilePasswordDto(UUID.randomUUID(), "123456", "654321", "654321");
        ChangeFilePasswordDto instance2 = new ChangeFilePasswordDto(UUID.randomUUID());
        assertTrue(instance1.isValid());
        assertTrue(instance2.isValid());
    }

    @Test
    @Order(5)
    @DisplayName("ChangeFilePasswordDto 'isValid' method returns true")
    public void assertIsValidReturnsFalseTest() {
        ChangeFilePasswordDto instance = new ChangeFilePasswordDto();
        assertFalse(instance.isValid());
    }
}
