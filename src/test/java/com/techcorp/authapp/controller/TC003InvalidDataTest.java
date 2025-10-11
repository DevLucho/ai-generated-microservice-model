package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TC003 - Tests para registro con datos inválidos
 * Validar HTTP 400 Bad Request cuando se envían datos que no cumplen las validaciones
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(com.techcorp.authapp.config.TestSecurityConfig.class)
@DisplayName("TC003 - Registro con Datos Inválidos")
class TC003InvalidDataTest {

    // Constants for repeated strings
    private static final String API_REGISTER_ENDPOINT = "/api/auth/register";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation Failed";
    private static final String VALID_PASSWORD = "password123";
    private static final String VALID_EMAIL = "test@techcorp.com";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_STATUS_CODE_PATH = "$.statusCode";
    private static final String JSON_ERROR_PATH = "$.error";
    private static final String JSON_FIELD_ERRORS_PATH = "$.fieldErrors";
    private static final String USERNAME_FIELD_ERROR_PATH = "$.fieldErrors[?(@.field == 'username')]";
    private static final String PASSWORD_FIELD_ERROR_PATH = "$.fieldErrors[?(@.field == 'password')]";
    private static final String EMAIL_FIELD_ERROR_PATH = "$.fieldErrors[?(@.field == 'emailAddress')]";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("TC003.1 - Username vacío debe retornar 400")
    void registerUserWhenUsernameIsBlankReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setUsername("");
        invalidUser.setPassword(VALID_PASSWORD);
        invalidUser.setEmailAddress(VALID_EMAIL);

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(USERNAME_FIELD_ERROR_PATH).exists());
    }

    @Test
    @DisplayName("TC003.2 - Username muy corto debe retornar 400")
    void registerUserWhenUsernameIsTooShortReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setUsername("ab"); // Solo 2 caracteres, mínimo 3
        invalidUser.setPassword(VALID_PASSWORD);
        invalidUser.setEmailAddress(VALID_EMAIL);

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(USERNAME_FIELD_ERROR_PATH).exists());
    }

    @Test
    @DisplayName("TC003.3 - Password muy corta debe retornar 400")
    void registerUserWhenPasswordIsTooShortReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setUsername("testuser");
        invalidUser.setPassword("12345"); // Solo 5 caracteres, mínimo 6
        invalidUser.setEmailAddress(VALID_EMAIL);

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(PASSWORD_FIELD_ERROR_PATH).exists());
    }

    @Test
    @DisplayName("TC003.4 - Email inválido debe retornar 400")
    void registerUserWhenEmailIsInvalidReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setUsername("testuser");
        invalidUser.setPassword(VALID_PASSWORD);
        invalidUser.setEmailAddress("invalid-email"); // Email sin formato válido

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(EMAIL_FIELD_ERROR_PATH).exists());
    }

    @Test
    @DisplayName("TC003.5 - Múltiples errores de validación debe retornar 400")
    void registerUserWhenMultipleFieldsAreInvalidReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setUsername(""); // Username vacío
        invalidUser.setPassword("123"); // Password muy corta
        invalidUser.setEmailAddress("bad-email"); // Email inválido

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(USERNAME_FIELD_ERROR_PATH).exists())
                .andExpect(jsonPath(PASSWORD_FIELD_ERROR_PATH).exists())
                .andExpect(jsonPath(EMAIL_FIELD_ERROR_PATH).exists());
    }

    @Test
    @DisplayName("TC003.6 - Campos null debe retornar 400")
    void registerUserWhenFieldsAreNullReturns400BadRequest() throws Exception {
        // Arrange
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        // Todos los campos quedan null

        // Act & Assert
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(400))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_MESSAGE));
    }
}
