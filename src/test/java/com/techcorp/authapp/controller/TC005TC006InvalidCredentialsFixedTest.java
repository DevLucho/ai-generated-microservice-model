package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.InvalidCredentialsException;
import com.techcorp.authapp.service.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests corregidos para casos de credenciales inválidas 
 * TC005: Password incorrecta
 * TC006: Usuario inexistente
 * Nota: El sistema devuelve HTTP 404 para credenciales inválidas según el manejo de excepciones configurado
 */
@WebMvcTest(UserAuthenticationController.class)
@DisplayName("TC005/TC006 - Credenciales Inválidas (Versión Corregida)")
class TC005TC006InvalidCredentialsFixedTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String API_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_DATA_PATH = "$.data";
    private static final String JSON_TIMESTAMP_PATH = "$.timestamp";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configurar comportamiento por defecto del mock
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));
    }

    @Test
    @DisplayName("TC005 - Password incorrecta debe retornar error")
    @WithMockUser
    void incorrectPasswordReturnsError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("valid.user");
        loginRequest.setPassword("wrong_password");

        // Mock configurado en @BeforeEach para InvalidCredentialsException
        
        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(INVALID_CREDENTIALS_MESSAGE))
                .andExpect(jsonPath(JSON_DATA_PATH).value(nullValue()))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("TC006 - Usuario inexistente debe retornar error con mensaje específico")
    @WithMockUser
    void nonExistentUserReturnsError() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("non.existent.user");
        loginRequest.setPassword("any_password");

        // Configurar mock para simular usuario no encontrado
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        
        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(USER_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath(JSON_DATA_PATH).value(nullValue()))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("TC005.1 - Múltiples passwords incorrectas deben retornar error")
    @WithMockUser
    void multipleIncorrectPasswordsReturnError() throws Exception {
        // Arrange
        String[] incorrectPasswords = {
            "wrong123",
            "incorrect456", 
            "badpass789",
            "invalid000"
        };

        for (String password : incorrectPasswords) {
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("valid.user");
            loginRequest.setPassword(password);

            // Mock ya configurado en @BeforeEach

            // Act & Assert
            mockMvc.perform(post(API_LOGIN_ENDPOINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(JSON_CONTENT_TYPE))
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                    .andExpect(jsonPath(JSON_MESSAGE_PATH).value(INVALID_CREDENTIALS_MESSAGE));
        }
    }

    @Test
    @DisplayName("TC006.1 - Múltiples usuarios inexistentes deben retornar error")
    @WithMockUser
    void multipleNonExistentUsersReturnError() throws Exception {
        // Arrange
        String[] nonExistentUsers = {
            "ghost.user",
            "fake.account",
            "invalid.username",
            "unknown.person"
        };

        for (String username : nonExistentUsers) {
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername(username);
            loginRequest.setPassword("any_password");

            // Configurar mock para cada usuario inexistente
            when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

            // Act & Assert
            mockMvc.perform(post(API_LOGIN_ENDPOINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(JSON_CONTENT_TYPE))
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                    .andExpect(jsonPath(JSON_MESSAGE_PATH).value(USER_NOT_FOUND_MESSAGE));
        }
    }
}