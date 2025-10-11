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
 * Test ultra-específico para cubrir las últimas 6 instrucciones faltantes
 * Objetivo: Alcanzar 100% de cobertura en UserAuthenticationController
 * 
 * Líneas objetivo:
 * - Línea 235: Branch faltante de "Credenciales inválidas" 
 * - Línea 240: Return BAD_REQUEST por defecto
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class Final100PercentCoverageTest {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

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
    @DisplayName("100% Coverage: RuntimeException without any specific keywords should return BAD_REQUEST")
    void testLoginRuntimeExceptionGenericErrorReturns400() throws Exception {
        // Arrange: RuntimeException sin palabras clave específicas para activar línea 240
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Network connection failed"));

        // Act & Assert: Debe activar el return BAD_REQUEST por defecto (línea 240)
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Network connection failed"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("100% Coverage: RuntimeException that does NOT contain Credenciales inválidas")
    void testLoginRuntimeExceptionNotCredencialesInvalidas() throws Exception {
        // Arrange: RuntimeException que NO contiene "Credenciales inválidas" 
        // Esto debe activar el branch else de la línea 235
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Sistema no disponible temporalmente"));

        // Act & Assert: Como no contiene ninguna palabra clave, debe ir a BAD_REQUEST
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sistema no disponible temporalmente"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }
}