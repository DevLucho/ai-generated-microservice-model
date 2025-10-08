package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Test de diagnóstico para encontrar el problema de autenticación
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Diagnóstico - Test de Usuario No Encontrado")
class DiagnosticTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reset(authenticationService);
    }

    @Test
    @WithMockUser
    @DisplayName("DEBUG - Verificar que el mock se aplica")
    void testMockIsWorking() throws Exception {
        // Arrange: Usuario que no existe
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("test.usuario");
        loginRequest.setPassword("password123");

        // Mock: Usuario no encontrado - RuntimeException con mensaje específico
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act: Intentar login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(result -> {
                    System.out.println("=== DEBUG RESPONSE ===");
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("Content: " + result.getResponse().getContentAsString());
                    System.out.println("===================");
                });
        
        // Verificar que el mock fue llamado
        verify(authenticationService, times(1)).authenticateUser(any(LoginRequestDto.class));
    }
}
