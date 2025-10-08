package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Test para validar las correcciones aplicadas según GAP_ANALYSIS_TC001.md
 * Validación del caso de prueba TC001: Registro Exitoso
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class TC001ValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    /**
     * TC001 - Registro Exitoso
     * Verifica que las correcciones GAP sean aplicadas correctamente:
     * - HTTP 200 OK (no 201)
     * - Mensaje "Registro exitoso" (no "User registered successfully")
     */
    @Test
    @WithMockUser
    void testTC001_RegistroExitoso_CorreccionsGAPAplicadas() throws Exception {
        // Reset mock to ensure clean state
        reset(authenticationService);
        
        // Arrange: Datos de entrada válidos según TC001
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("usuario.valido");
        registrationDto.setPassword("password123");
        registrationDto.setEmailAddress("usuario@techcorp.com");

        // Mock del usuario registrado exitosamente
        SystemUser mockUser = new SystemUser();
        mockUser.setUserId("USR-12345");
        mockUser.setUsername("usuario.valido");
        mockUser.setEmailAddress("usuario@techcorp.com");
        mockUser.setRegistrationDate(LocalDateTime.now());

        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenReturn(mockUser);

        String requestBody = objectMapper.writeValueAsString(registrationDto);

        // Act & Assert: Verificar correcciones GAP
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                
                // CORRECCIÓN GAP 1: Verificar HTTP 200 OK (no 201)
                .andExpect(status().isOk())
                
                // CORRECCIÓN GAP 2: Verificar mensaje "Registro exitoso" 
                .andExpect(jsonPath("$.message").value("Registro exitoso"))
                
                // Validaciones adicionales de estructura
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("USR-12345"))
                .andExpect(jsonPath("$.data.username").value("usuario.valido"))
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
}
