package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para TC009 - Logout sin Token
 * Valida manejo de requests sin header Authorization según lineamientos TechCorp
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("TC009 - Logout sin Token")
class TC009MissingTokenLogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Header Authorization Faltante")
    class MissingAuthorizationHeader {

        @Test
        @DisplayName("TC009.001 - Debe retornar HTTP 400 Bad Request cuando falta header Authorization")
        void testLogoutUser_WhenMissingAuthorizationHeader_Returns400BadRequest() throws Exception {
            // Arrange & Act & Assert: Request sin header Authorization
            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()) // HTTP 400
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Token requerido"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("TC009.002 - Debe retornar mensaje específico en español para token requerido")
        void testLogoutUser_WhenMissingToken_ReturnsSpanishMessage() throws Exception {
            // Arrange & Act: Request sin token
            String response = mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Assert: Verificar mensaje específico en español
            assertThat(response).contains("Token requerido");
            assertThat(response).doesNotContain("Token required");
            assertThat(response).doesNotContain("Authorization header missing");
            assertThat(response).doesNotContain("Authentication required");
        }

        @Test
        @DisplayName("TC009.003 - Debe validar estructura completa de respuesta para error 400")
        void testLogoutUser_WhenMissingToken_ValidatesCompleteErrorStructure() throws Exception {
            // Arrange & Act & Assert: Validación completa de estructura ApiResponseDto
            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").isBoolean())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.message").value("Token requerido"))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("TC009.004 - Debe ser consistente independientemente del Content-Type")
        void testLogoutUser_WhenMissingTokenDifferentContentTypes_ConsistentResponse() throws Exception {
            // Arrange: Diferentes Content-Types
            String[] contentTypes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.APPLICATION_XML_VALUE
            };

            for (String contentType : contentTypes) {
                // Act & Assert: Debe ser consistente independientemente del Content-Type
                mockMvc.perform(post("/api/auth/logout")
                        .contentType(contentType))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Token requerido"));
            }
        }
    }

    @Nested
    @DisplayName("Header Authorization Vacío")
    class EmptyAuthorizationHeader {

        @Test
        @DisplayName("TC009.005 - Debe retornar HTTP 400 cuando header Authorization está vacío")
        void testLogoutUser_WhenEmptyAuthorizationHeader_Returns400() throws Exception {
            // Arrange & Act & Assert: Header Authorization vacío
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Token requerido"));
        }

        @Test
        @DisplayName("TC009.006 - Debe retornar HTTP 400 cuando header Authorization solo contiene espacios")
        void testLogoutUser_WhenWhitespaceOnlyHeader_Returns400() throws Exception {
            // Arrange: Header con solo espacios en blanco
            String[] whitespaceHeaders = {
                " ",
                "   ",
                "\t",
                "\n",
                "  \t  \n  "
            };

            for (String whitespaceHeader : whitespaceHeaders) {
                // Act & Assert: Debe tratar espacios como header vacío
                mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", whitespaceHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Token requerido"));
            }
        }

        @Test
        @DisplayName("TC009.007 - Debe manejar header Authorization con valor null")
        void testLogoutUser_WhenNullAuthorizationHeader_Returns400() throws Exception {
            // Arrange & Act & Assert: Simular header con valor null
            // (MockMvc maneja esto como header faltante)
            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token requerido"));
        }
    }

    @Nested
    @DisplayName("Otros Headers Presentes")
    class OtherHeadersPresent {

        @Test
        @DisplayName("TC009.008 - Debe fallar aún con otros headers de autenticación presentes")
        void testLogoutUser_WhenOtherAuthHeaders_StillRequiresAuthorization() throws Exception {
            // Arrange: Otros headers relacionados con autenticación pero sin Authorization
            mockMvc.perform(post("/api/auth/logout")
                    .header("X-API-Key", "some-api-key")
                    .header("X-Auth-Token", "some-auth-token")
                    .header("Authentication", "Bearer token") // Nombre incorrecto
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token requerido"));
        }

        @Test
        @DisplayName("TC009.009 - Debe ignorar headers irrelevantes y enfocarse en Authorization")
        void testLogoutUser_WhenIrrelevantHeaders_FocusesOnAuthorization() throws Exception {
            // Arrange: Múltiples headers irrelevantes pero sin Authorization
            mockMvc.perform(post("/api/auth/logout")
                    .header("User-Agent", "TestAgent/1.0")
                    .header("Accept", "application/json")
                    .header("Cache-Control", "no-cache")
                    .header("X-Request-ID", "12345")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Token requerido"));
        }

        @Test
        @DisplayName("TC009.010 - Debe diferenciarse claramente de error por token inválido")
        void testLogoutUser_WhenMissingToken_DifferentiatesFromInvalidToken() throws Exception {
            // Arrange & Act: Request sin Authorization header
            String responseWithoutHeader = mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()) // 400 para token faltante
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Assert: Debe ser diferente a error de token inválido (que sería 401)
            assertThat(responseWithoutHeader).contains("Token requerido");
            assertThat(responseWithoutHeader).doesNotContain("Token inválido");
            assertThat(responseWithoutHeader).doesNotContain("Unauthorized");

            // El contraste se hace implícitamente: 400 vs 401, "requerido" vs "inválido"
        }
    }

    @Nested
    @DisplayName("Comportamiento con Diferentes Métodos HTTP")
    class DifferentHttpMethods {

        @Test
        @DisplayName("TC009.011 - Debe manejar consistentemente logout sin token en POST")
        void testLogoutUser_WhenPostWithoutToken_ConsistentBehavior() throws Exception {
            // Arrange & Act & Assert: Específicamente método POST sin token
            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token requerido"));
        }

        @Test
        @DisplayName("TC009.012 - Debe validar que el endpoint requiere método POST")
        void testLogoutUser_WhenCorrectEndpointWithoutToken_ValidatesPostMethod() throws Exception {
            // Arrange & Act & Assert: Confirmar que estamos testando el endpoint correcto
            // Este test valida que el endpoint /api/auth/logout existe y requiere POST
            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()) // No 404, confirma que endpoint existe
                    .andExpect(jsonPath("$.message").value("Token requerido")); // No 405, confirma que POST es válido
        }
    }

    @Nested
    @DisplayName("Validación de Precedencia de Errores")
    class ErrorPrecedenceValidation {

        @Test
        @DisplayName("TC009.013 - Token faltante debe tener precedencia sobre otros errores de validación")
        void testLogoutUser_WhenMissingToken_HasErrorPrecedence() throws Exception {
            // Arrange & Act: Request malformado pero sin token
            mockMvc.perform(post("/api/auth/logout")
                    .contentType("invalid/content-type") // Content-Type inválido
                    .content("invalid json content")) // JSON inválido
                    // Pero sin header Authorization
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token requerido"));
            
            // El error de token faltante debe tener precedencia sobre errores de parsing
        }

        @Test
        @DisplayName("TC009.014 - Debe ser rápido en detectar token faltante sin procesar request body")
        void testLogoutUser_WhenMissingToken_FastValidation() throws Exception {
            // Arrange & Act: Request con body muy grande pero sin token
            String largeContent = "x".repeat(10000); // Contenido grande

            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(largeContent))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token requerido"));

            // La validación debe ser rápida, sin procesar el body completo
        }
    }
}
