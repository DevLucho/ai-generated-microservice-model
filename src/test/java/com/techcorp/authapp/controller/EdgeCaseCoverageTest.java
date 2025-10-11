package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests específicos para alcanzar 100% de cobertura en UserAuthenticationController
 * Enfoque en líneas no cubiertas identificadas en reporte Jacoco
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class EdgeCaseCoverageTest {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;
    private static final String JSON_PATH_SUCCESS = "$.success";
    private static final String JSON_PATH_MESSAGE = "$.message";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(USERNAME);
        loginRequestDto.setPassword(PASSWORD);
    }

    @Test
    @DisplayName("Login should return UNAUTHORIZED for RuntimeException with Credenciales inválidas message")
    void testLoginRuntimeExceptionWithCredencialesInvalidas() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje específico (no InvalidCredentialsException)
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert: validar manejo del RuntimeException con mensaje "Credenciales inválidas" (línea 235)
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Credenciales inválidas"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login should return BAD_REQUEST for RuntimeException with generic message")
    void testLoginRuntimeExceptionGenericMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje genérico
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Unexpected database error"));

        // Act & Assert: validar manejo del RuntimeException genérico (línea 240)
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Unexpected database error"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Logout should return UNAUTHORIZED for RuntimeException with Token message")
    void testLogoutRuntimeExceptionWithTokenMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje "Token"
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Token expired"));

        // Act & Assert: validar manejo del RuntimeException con "Token" (línea 326 - primera condición)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Token inválido"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
    }

    @Test
    @DisplayName("Logout should return UNAUTHORIZED for RuntimeException with Invalid message")
    void testLogoutRuntimeExceptionWithInvalidMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje "Invalid"
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Invalid token signature"));

        // Act & Assert: validar manejo del RuntimeException con "Invalid" (línea 326 - segunda condición)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Token inválido"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
    }

    @Test
    @DisplayName("Logout should return UNAUTHORIZED for RuntimeException with inválido message")
    void testLogoutRuntimeExceptionWithInvalidoMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje "inválido"
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Token inválido para usuario"));

        // Act & Assert: validar manejo del RuntimeException con "inválido" (línea 326 - tercera condición)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Token inválido"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
    }

    @Test
    @DisplayName("Logout should return BAD_REQUEST for RuntimeException with generic message")
    void testLogoutRuntimeExceptionGenericMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException con mensaje genérico que no contiene palabras clave
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert: validar manejo del RuntimeException genérico en logout (línea 330)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Logout failed"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
    }

    @Test
    @DisplayName("Logout should handle RuntimeException in logoutUser method")
    void testLogoutRuntimeExceptionInLogoutUser() throws Exception {
        // Arrange: mock para validación exitosa pero error en logout
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenReturn(USERNAME);
        doThrow(new RuntimeException("Database error during logout"))
            .when(authenticationService).logoutUser(USERNAME);

        // Act & Assert: validar manejo del RuntimeException en logoutUser (línea 330)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Logout failed"));

        // Verificar que ambos métodos del servicio fueron llamados
        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService).logoutUser(USERNAME);
    }
}