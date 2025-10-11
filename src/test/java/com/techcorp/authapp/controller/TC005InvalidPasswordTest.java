package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para TC005 - Login con Contraseña Incorrecta  
 * Valida manejo de credenciales inválidas según lineamientos TechCorp
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("TC005 - Login con Contraseña Incorrecta")
class TC005InvalidPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Validación de Contraseña Incorrecta")
    class IncorrectPasswordValidation {

        @BeforeEach
        void setUp() {
            // Reset mock before each test to ensure clean state
            reset(authenticationService);
            
            // Configure mock to throw InvalidCredentialsException for ANY authentication call
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));
            
            // Also configure register to work normally (mock it to avoid side effects)
            when(authenticationService.registerNewUser(any()))
                .thenThrow(new RuntimeException("No register calls expected in these tests"));
        }

        @Test
        @DisplayName("TC005.001 - Debe retornar HTTP 401 cuando la contraseña es incorrecta")
        void testLoginUser_WhenIncorrectPassword_Returns401Unauthorized() throws Exception {
            // Arrange: Contraseña incorrecta
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("juan.perez");
            loginRequest.setPassword("wrongpassword");

            // Act & Assert: Debe retornar 401 Unauthorized
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized()) // HTTP 401
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                    .andExpect(jsonPath("$.data").value((Object) null));
        }

        @Test
        @DisplayName("TC005.002 - Debe retornar mensaje de error en español")
        void testLoginUser_WhenIncorrectPassword_ReturnsSpanishErrorMessage() throws Exception {
            // Arrange: Datos con contraseña incorrecta
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("maria.rodriguez");
            loginRequest.setPassword("badpassword");

            // Act & Assert: Verificar mensaje específico en español
            String response = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Validar mensaje en español
            assertThat(response).contains("Credenciales inválidas");
            assertThat(response).doesNotContain("Invalid credentials");
            assertThat(response).doesNotContain("authentication failed");
        }

        @Test
        @DisplayName("TC005.003 - Debe validar estructura completa del error de contraseña incorrecta")
        void testLoginUser_WhenIncorrectPassword_ValidatesCompleteErrorStructure() throws Exception {
            // Arrange: Datos para contraseña incorrecta
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("carlos.martinez");
            loginRequest.setPassword("incorrectpassword");

            // Act & Assert: Validación completa de estructura ApiResponseDto
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").isBoolean())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                    .andExpect(jsonPath("$.data").value((Object) null))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("TC005.004 - Debe manejar diferentes tipos de contraseñas incorrectas")
        void testLoginUser_WhenDifferentIncorrectPasswords_AllReturn401() throws Exception {
            // Arrange: Diferentes tipos de contraseñas incorrectas (pero válidas en formato)
            String[] incorrectPasswords = {
                "wrong",
                "password123wrong",
                "WRONGPASSWORD",
                "123456789",
                "admin"
                // Nota: password vacío se maneja en SecurityConsistency como caso 400
            };

            // Act & Assert: Todos deben devolver 401
            for (String password : incorrectPasswords) {
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setUsername("test.user");
                loginRequest.setPassword(password);

                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                        .andExpect(jsonPath("$.data").value((Object) null));
            }
        }
    }
}
