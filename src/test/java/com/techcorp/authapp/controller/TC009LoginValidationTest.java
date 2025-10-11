package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para validación de datos faltantes en login
 * TC009: Validación de campos requeridos en request de login
 */
@WebMvcTest(UserAuthenticationController.class)
@DisplayName("TC009 - Validación Login Datos Faltantes")
class TC009LoginValidationTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String API_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_STATUS_CODE_PATH = "$.statusCode";
    private static final String JSON_ERROR_PATH = "$.error";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_TIMESTAMP_PATH = "$.timestamp";
    private static final String JSON_FIELD_ERRORS_PATH = "$.fieldErrors";
    private static final int BAD_REQUEST_STATUS = 400;
    private static final String VALIDATION_FAILED_ERROR = "Validation Failed";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TC009.1 - Login sin username debe retornar error de validación")
    @WithMockUser
    void loginWithoutUsernameReturnsValidationError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setPassword("validPassword123");
        // username es null

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(BAD_REQUEST_STATUS))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_ERROR))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("TC009.2 - Login sin password debe retornar error de validación")
    @WithMockUser
    void loginWithoutPasswordReturnsValidationError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("validUser");
        // password es null

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(BAD_REQUEST_STATUS))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_ERROR))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("TC009.3 - Login sin username ni password debe retornar error de validación")
    @WithMockUser
    void loginWithoutUsernameAndPasswordReturnsValidationError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        // Ambos campos null

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(BAD_REQUEST_STATUS))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_ERROR))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("TC009.4 - Login con campos vacíos debe retornar error de validación")
    @WithMockUser
    void loginWithEmptyFieldsReturnsValidationError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_STATUS_CODE_PATH).value(BAD_REQUEST_STATUS))
                .andExpect(jsonPath(JSON_ERROR_PATH).value(VALIDATION_FAILED_ERROR))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                .andExpect(jsonPath(JSON_FIELD_ERRORS_PATH).exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }
}