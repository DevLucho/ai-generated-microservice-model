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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test específico para alcanzar 100% de cobertura en UserAuthenticationController
 * Enfocado en las líneas 235 y 240 identificadas en el reporte Jacoco
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class LoginRuntimeExceptionCoverageTest {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String JSON_PATH_SUCCESS = "$.success";
    private static final String JSON_PATH_MESSAGE = "$.message";

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
    @DisplayName("Login should return BAD_REQUEST for RuntimeException without specific keywords")
    void testLoginRuntimeExceptionGenericMessage() throws Exception {
        // Arrange: mock para lanzar RuntimeException sin palabras clave específicas
        // Esto cubrirá la línea 240 (return BAD_REQUEST por defecto)
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Database connection timeout"));

        // Act & Assert: validar que se devuelve BAD_REQUEST para errores genéricos
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Database connection timeout"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login should return UNAUTHORIZED for RuntimeException with Credenciales inválidas - branch coverage")
    void testLoginRuntimeExceptionCredencialesInvalidasBranch() throws Exception {
        // Arrange: mock para lanzar RuntimeException que NO entra en la condición "Credenciales inválidas"
        // Esto cubrirá el branch faltante en la línea 235
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Usuario no encontrado en la base de datos"));

        // Act & Assert: validar que se procesa correctamente el mensaje "Usuario no encontrado"
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Usuario no encontrado en la base de datos"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login should cover the else branch for Credenciales inválidas condition")
    void testLoginRuntimeExceptionCredencialesInvalidasElseBranch() throws Exception {
        // Arrange: mock para lanzar RuntimeException que SÍ contiene "Credenciales inválidas"
        // Esto cubrirá el branch que entra en la condición de la línea 235
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Credenciales inválidas para el usuario"));

        // Act & Assert: validar que se devuelve UNAUTHORIZED para credenciales inválidas
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Credenciales inválidas para el usuario"));

        // Verificar que el servicio fue llamado
        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }
}