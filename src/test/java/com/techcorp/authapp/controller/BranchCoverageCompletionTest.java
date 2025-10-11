package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests ultraespecíficos para cubrir las branches faltantes (operadores || y &&)
 * Objetivo: Lograr 100% de cobertura de branches en UserAuthenticationController
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class BranchCoverageCompletionTest {

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String SUCCESS_PATH = "$.success";
    private static final String MESSAGE_PATH = "$.message";
    private static final String TEST_PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test para cubrir la branch faltante en registerUser línea 130:
     * Condición: e.getMessage().contains("already exists") || e.getMessage().contains("ya registrado")
     * Caso específico: RuntimeException con mensaje que contiene "ya registrado" pero NO "already exists"
     */
    @Test
    void registerUserWithYaRegistradoMessageShouldReturnConflict() throws Exception {
        // Arrange: RuntimeException con mensaje que contiene solo "ya registrado"
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser", "test@example.com", TEST_PASSWORD
        );
        
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Email ya registrado en el sistema"));

        // Act & Assert: debe retornar HTTP 409 CONFLICT
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(MESSAGE_PATH).value("Email ya registrado en el sistema"));
    }

    /**
     * Test para cubrir la branch faltante en registerUser línea 130:
     * Caso específico: RuntimeException con mensaje que contiene "already exists" pero NO "ya registrado"
     */
    @Test
    void registerUserWithAlreadyExistsMessageShouldReturnConflict() throws Exception {
        // Arrange: RuntimeException con mensaje que contiene solo "already exists"
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "duplicateuser", "duplicate@example.com", TEST_PASSWORD
        );
        
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Username already exists in database"));

        // Act & Assert: debe retornar HTTP 409 CONFLICT
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(MESSAGE_PATH).value("Username already exists in database"));
    }

    /**
     * Test para cubrir branch faltante con RuntimeException que NO contiene ni "already exists" ni "ya registrado"
     * Debe ir por el path del return BAD_REQUEST por defecto
     */
    @Test
    void registerUserWithoutKeywordsShouldReturnBadRequest() throws Exception {
        // Arrange: RuntimeException sin palabras clave específicas
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "erroruser", "error@example.com", TEST_PASSWORD
        );
        
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Error de conexión a base de datos"));

        // Act & Assert: debe retornar HTTP 400 BAD_REQUEST por defecto
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(MESSAGE_PATH).value("Error de conexión a base de datos"));
    }

    /**
     * Test para investigar y cubrir la branch faltante en loginUser
     * Probando RuntimeException sin palabras clave específicas para activar path alternativo
     */
    @Test
    void loginUserWithUnknownMessageShouldReturnBadRequest() throws Exception {
        // Arrange: RuntimeException con mensaje que no coincide con ningún if
        LoginRequestDto loginRequest = new LoginRequestDto("unknownuser", "password");
        
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Error inesperado del sistema"));

        // Act & Assert: debe ir por el path BAD_REQUEST por defecto
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(MESSAGE_PATH).value("Error inesperado del sistema"));
    }

    /**
     * Test para verificar RuntimeException con mensaje parcial que puede afectar branches
     */
    @Test
    void loginUserWithPartialMatchShouldReturnUnauthorized() throws Exception {
        // Arrange: RuntimeException con mensaje que podría afectar la evaluación de conditions
        LoginRequestDto loginRequest = new LoginRequestDto("partialuser", "password");
        
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas parcialmente"));

        // Act & Assert: debe activar el if de "Credenciales inválidas" y retornar UNAUTHORIZED
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(SUCCESS_PATH).value(false))
                .andExpect(jsonPath(MESSAGE_PATH).value("Credenciales inválidas parcialmente"));
    }
}