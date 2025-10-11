package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.UserNotFoundException;
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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para TC006 - Login con Usuario Inexistente
 * Valida manejo de usuarios no registrados según lineamientos TechCorp
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("TC006 - Login con Usuario Inexistente")
class TC006UserNotFoundTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Reset mock antes de cada test para asegurar estado limpio
        reset(authenticationService);
    }

    @Nested
    @DisplayName("Usuario Inexistente")
    class UserNotFoundValidation {

        @BeforeEach
        void setUpNested() {
            // Reset mock específico para esta clase nested
            reset(authenticationService);
            
            // Configure mock to throw UserNotFoundException for user not found
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));
        }

        @Test
        @DisplayName("TC006.001 - Debe retornar HTTP 404 Not Found para usuario inexistente")
        void testLoginUser_WhenUserNotFound_Returns404NotFound() throws Exception {
            // Arrange: Usuario que no existe en el sistema
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("inexistente.usuario");
            loginRequest.setPassword("anypassword");

            // Act & Assert: Debe retornar 404 Not Found
            mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound()) // HTTP 404
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                    .andExpect(jsonPath("$.data").value((Object) null));
        }

        @Test
        @DisplayName("TC006.002 - Debe retornar mensaje específico en español para usuario no encontrado")
        void testLoginUser_WhenUserNotFound_ReturnsSpanishMessage() throws Exception {
            // Arrange: Usuario inexistente
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("noexiste.usuario");
            loginRequest.setPassword("password123");

            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Act & Assert: Verificar mensaje específico en español
            String response = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Validar mensaje en español y específico
            assertThat(response).contains("Usuario no encontrado");
            assertThat(response).doesNotContain("User not found");
            assertThat(response).doesNotContain("Invalid credentials");
            assertThat(response).doesNotContain("does not exist");
        }

        @Test
        @DisplayName("TC006.003 - Debe validar estructura completa de respuesta para error 404")
        void testLoginUser_WhenUserNotFound_ValidatesCompleteErrorStructure() throws Exception {
            // Arrange: Setup para validar estructura completa de error 404
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("notfound.user");
            loginRequest.setPassword("testpassword");

            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Act & Assert: Validar estructura completa de respuesta
            String response = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success").isBoolean())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                    .andExpect(jsonPath("$.data").value((Object) null))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Validaciones adicionales sobre el contenido
            assertThat(response).contains("success");
            assertThat(response).contains("message");
            assertThat(response).contains("data");
            assertThat(response).contains("timestamp");
        }

        @Test
        @DisplayName("TC006.004 - Debe manejar diferentes formatos de usernames inexistentes")
        void testLoginUser_WhenDifferentNonExistentUsers_AllReturn404() throws Exception {
            // Arrange: Configurar mock una sola vez
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Diferentes formatos de usernames que no existen (únicos para evitar coincidencias)
            String[] nonExistentUsers = {
                "fake.user.notfound",
                "admin123.inexistente", 
                "test@user.noexiste",
                "user_not_found_definitely",
                "nonexistent-admin-sure",
                "ghost.user.nothere",
                "deleted.user.gone"
            };

            for (String username : nonExistentUsers) {
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setUsername(username);
                loginRequest.setPassword("anypassword");

                // Act & Assert: Todos deben retornar 404
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
            }
        }
    }

    @Nested
    @DisplayName("Diferenciación de Errores")
    class ErrorDifferentiation {

        @BeforeEach
        void setUpNested() {
            // Reset mock específico para esta clase nested
            reset(authenticationService);
            
            // Configure mock to throw UserNotFoundException for user not found
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));
        }

        @Test
        @DisplayName("TC006.005 - Debe diferenciarse claramente de error de contraseña incorrecta")
        void testLoginUser_WhenUserNotFound_DifferentiatesFromPasswordError() throws Exception {
            // Arrange: Comparar respuestas de usuario inexistente vs contraseña incorrecta
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("test.comparison");
            loginRequest.setPassword("password123");

            // Simular usuario inexistente
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Act & Assert: Debe ser específico para usuario inexistente
            String response = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verificar que el mensaje es específico para usuario no encontrado
            assertThat(response).contains("Usuario no encontrado");
            assertThat(response).doesNotContain("Credenciales inválidas");
            assertThat(response).doesNotContain("password");
            assertThat(response).doesNotContain("contraseña");
        }

        @Test
        @DisplayName("TC006.006 - Debe manejar correctamente case sensitivity en username inexistente")
        void testLoginUser_WhenCaseSensitiveNonExistentUser_Returns404() throws Exception {
            // Arrange: Configurar mock una sola vez
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Usernames inexistentes con diferentes casos (únicos para evitar coincidencias)
            String[] caseVariations = {
                "NONEXISTENT.USER.NOTFOUND",
                "NonExistent.User.Gone",
                "nonexistent.USER.missing",
                "NonExistent.USER.deleted"
            };

            for (String username : caseVariations) {
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setUsername(username);
                loginRequest.setPassword("password123");

                // Act & Assert: Todos deben retornar 404 consistentemente
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
            }
        }

        @Test
        @DisplayName("TC006.007 - Debe ser específico en mensaje de error sin revelar información del sistema")
        void testLoginUser_WhenUserNotFound_SecureErrorMessage() throws Exception {
            // Arrange: Usuario inexistente
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("hacker.attempt.notreal");
            loginRequest.setPassword("tryingtohack");

            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Act & Assert: Verificar mensaje seguro
            String response = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verificar que no se revela información del sistema
            assertThat(response).contains("Usuario no encontrado");
            assertThat(response).doesNotContain("database");
            assertThat(response).doesNotContain("table");
            assertThat(response).doesNotContain("sql");
            assertThat(response).doesNotContain("repository");
            assertThat(response).doesNotContain("id");
        }

        @Test
        @DisplayName("TC006.008 - Debe manejar usernames con caracteres especiales inexistentes")
        void testLoginUser_WhenSpecialCharactersInNonExistentUser_Returns404() throws Exception {
            // Arrange: Configurar mock una sola vez
            when(authenticationService.authenticateUser(any()))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

            // Usernames inexistentes con caracteres especiales (únicos para evitar coincidencias)
            String[] specialCharUsers = {
                "user@domain.notfound",
                "user#123.noexiste",
                "user$pecial.inexistente",
                "user%test.nothere",
                "user&name.gone",
                "user*star.deleted",
                "user(test).missing",
                "user+plus.nowhere"
            };

            for (String username : specialCharUsers) {
                LoginRequestDto loginRequest = new LoginRequestDto();
                loginRequest.setUsername(username);
                loginRequest.setPassword("password123");

                // Act & Assert: Debe manejar caracteres especiales correctamente
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
            }
        }
    }
}
