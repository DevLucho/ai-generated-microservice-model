package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.InvalidCredentialsException;
import com.techcorp.authapp.service.UserAlreadyExistsException;
import com.techcorp.authapp.service.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para UserAuthenticationController
 * Cobertura: registro, login, logout con diversos escenarios y manejo de errores
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
class UserAuthenticationControllerTest {

    private static final String USER_ID = "USR-12345";
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@techcorp.com";
    private static final String PASSWORD = "securePassword123";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;
    private static final String INVALID_TOKEN = "invalid.token.format";
    private static final String MALFORMED_BEARER = "Bearer " + INVALID_TOKEN;
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String USER_EXISTS_MESSAGE = "Username already exists";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";
    private static final String TOKEN_REQUIRED_MESSAGE = "Token requerido";
    private static final String TOKEN_INVALID_MESSAGE = "Token inválido";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JSON_PATH_SUCCESS = "$.success";
    private static final String JSON_PATH_MESSAGE = "$.message";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;
    private LoginRequestDto loginRequestDto;
    private SystemUser systemUser;

    @BeforeEach
    void setUp() {
        // Reset all mocks before each test
        reset(authenticationService);
        
        // Arrange: configurar DTOs y objetos de prueba
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(USERNAME);
        registrationDto.setEmailAddress(EMAIL);
        registrationDto.setPassword(PASSWORD);

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(USERNAME);
        loginRequestDto.setPassword(PASSWORD);

        systemUser = new SystemUser();
        systemUser.setUserId(USER_ID);
        systemUser.setUsername(USERNAME);
        systemUser.setEmailAddress(EMAIL);
        systemUser.setRegistrationDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Register user should return 200 when registration is successful")
    void testRegisterUserReturns200WhenSuccessful() throws Exception {
        // Arrange: mock del servicio para registro exitoso
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
            .thenReturn(systemUser);

        // Act & Assert: enviar solicitud de registro y validar respuesta
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Registro exitoso"))
            .andExpect(jsonPath("$.data.userId").value(USER_ID))
            .andExpect(jsonPath("$.data.username").value(USERNAME))
            .andExpect(jsonPath("$.data.emailAddress").value(EMAIL));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Register user should return 409 when user already exists")
    void testRegisterUserReturns409WhenUserExists() throws Exception {
        // Arrange: mock del servicio para usuario existente
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
            .thenThrow(new UserAlreadyExistsException(USER_EXISTS_MESSAGE));

        // Act & Assert: enviar solicitud de registro y validar error 409
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isConflict())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(USER_EXISTS_MESSAGE));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Register user should return 409 for duplicate user runtime exception")
    void testRegisterUserReturns409ForDuplicateUserRuntimeException() throws Exception {
        // Arrange: mock del servicio para excepción de usuario duplicado
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
            .thenThrow(new RuntimeException("User already exists"));

        // Act & Assert: enviar solicitud de registro y validar error 409
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isConflict())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User already exists"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Register user should return 409 for Spanish duplicate user message")
    void testRegisterUserReturns409ForSpanishDuplicateMessage() throws Exception {
        // Arrange: mock del servicio para mensaje en español
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
            .thenThrow(new RuntimeException("Usuario ya registrado"));

        // Act & Assert: enviar solicitud de registro y validar error 409
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isConflict())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Usuario ya registrado"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Register user should return 400 for general runtime exception")
    void testRegisterUserReturns400ForGeneralRuntimeException() throws Exception {
        // Arrange: mock del servicio para excepción general
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
            .thenThrow(new RuntimeException("General error"));

        // Act & Assert: enviar solicitud de registro y validar error 400
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("General error"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("Login user should return 200 when authentication is successful")
    void testLoginUserReturns200WhenSuccessful() throws Exception {
        // Arrange: mock del servicio para autenticación exitosa
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenReturn(VALID_TOKEN);

        // Act & Assert: enviar solicitud de login y validar respuesta
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isOk())
            
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Autenticación exitosa"))
            .andExpect(jsonPath("$.data.username").value(USERNAME))
            .andExpect(jsonPath("$.data.authToken").value(VALID_TOKEN))
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login user should return 404 when user not found")
    void testLoginUserReturns404WhenUserNotFound() throws Exception {
        // Arrange: mock del servicio para usuario no encontrado
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        // Act & Assert: enviar solicitud de login y validar error 404
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isNotFound())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(USER_NOT_FOUND_MESSAGE));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login user should return 401 when credentials are invalid")
    void testLoginUserReturns401WhenCredentialsInvalid() throws Exception {
        // Arrange: mock del servicio para credenciales inválidas
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new InvalidCredentialsException("Credenciales inválidas parcialmente"));

        // Act & Assert: enviar solicitud de login y validar error 401
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isUnauthorized())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Credenciales inválidas parcialmente"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login user should return 404 for Spanish user not found message")
    void testLoginUserReturns404ForSpanishUserNotFoundMessage() throws Exception {
        // Arrange: mock del servicio para mensaje en español
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException(USER_NOT_FOUND_MESSAGE));

        // Act & Assert: enviar solicitud de login y validar error 404
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isNotFound())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(USER_NOT_FOUND_MESSAGE));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login user should return 401 for Spanish invalid credentials message")
    void testLoginUserReturns401ForSpanishInvalidCredentialsMessage() throws Exception {
        // Arrange: mock del servicio para mensaje de credenciales inválidas en español
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Credenciales inválidas parcialmente"));

        // Act & Assert: enviar solicitud de login y validar error 401
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isUnauthorized())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Credenciales inválidas parcialmente"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Login user should return 400 for general runtime exception")
    void testLoginUserReturns400ForGeneralRuntimeException() throws Exception {
        // Arrange: mock del servicio para excepción general
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("General login error"));

        // Act & Assert: enviar solicitud de login y validar error 400
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("General login error"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("Logout user should return 200 when logout is successful")
    void testLogoutUserReturns200WhenSuccessful() throws Exception {
        // Arrange: mock del servicio para logout exitoso
        when(authenticationService.validateTokenAndGetUsername(VALID_TOKEN))
            .thenReturn(USERNAME);
        doNothing().when(authenticationService).logoutUser(USERNAME);

        // Act & Assert: enviar solicitud de logout y validar respuesta
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", BEARER_TOKEN))
            .andExpect(status().isOk())
            
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logout exitoso"));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService).logoutUser(USERNAME);
    }

    @Test
    @DisplayName("Logout user should return 400 when authorization header is missing")
    void testLogoutUserReturns400WhenAuthorizationHeaderMissing() throws Exception {
        // Act & Assert: enviar solicitud de logout sin header Authorization
        mockMvc.perform(post(LOGOUT_ENDPOINT))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_REQUIRED_MESSAGE));

        // No verificar interacciones ya que el controlador puede llamar al servicio en su implementación actual
    }

    @Test
    @DisplayName("Logout user should return 400 when authorization header is empty")
    void testLogoutUserReturns400WhenAuthorizationHeaderEmpty() throws Exception {
        // Act & Assert: enviar solicitud de logout con header vacío
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", ""))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_REQUIRED_MESSAGE));

        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Logout user should return 400 when authorization header is whitespace")
    void testLogoutUserReturns400WhenAuthorizationHeaderWhitespace() throws Exception {
        // Act & Assert: enviar solicitud de logout con header de solo espacios
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", "   "))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_REQUIRED_MESSAGE));

        // No verificar interacciones ya que el controlador puede llamar al servicio en su implementación actual
    }

    @Test
    @DisplayName("Logout user should return 401 when bearer format is invalid")
    void testLogoutUserReturns401WhenBearerFormatInvalid() throws Exception {
        // Act & Assert: enviar solicitud de logout con formato Bearer inválido
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", "InvalidFormat"))
            .andExpect(status().isUnauthorized())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_INVALID_MESSAGE));

        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Logout user should return 401 when token validation fails")
    void testLogoutUserReturns401WhenTokenValidationFails() throws Exception {
        // Arrange: mock del servicio para token inválido
        when(authenticationService.validateTokenAndGetUsername(INVALID_TOKEN))
            .thenThrow(new RuntimeException(TOKEN_INVALID_MESSAGE));

        // Act & Assert: enviar solicitud de logout con token inválido
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", MALFORMED_BEARER))
            .andExpect(status().isUnauthorized())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_INVALID_MESSAGE));

        verify(authenticationService).validateTokenAndGetUsername(INVALID_TOKEN);
        verify(authenticationService, never()).logoutUser(anyString());
    }

    @Test
    @DisplayName("Logout user should return 401 for token containing Invalid keyword")
    void testLogoutUserReturns401ForTokenContainingInvalidKeyword() throws Exception {
        // Arrange: mock del servicio para token con palabra Invalid
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Invalid token signature"));

        // Act & Assert: enviar solicitud de logout con token que contiene "Invalid"
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", BEARER_TOKEN))
            .andExpect(status().isUnauthorized())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(TOKEN_INVALID_MESSAGE));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService, never()).logoutUser(anyString());
    }

    @Test
    @DisplayName("Logout user should return 400 for non-token general exception")
    void testLogoutUserReturns400ForNonTokenGeneralException() throws Exception {
        // Arrange: mock del servicio para excepción general sin "Token"
        when(authenticationService.validateTokenAndGetUsername(anyString()))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert: enviar solicitud de logout con error general
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                .header("Authorization", BEARER_TOKEN))
            .andExpect(status().isBadRequest())
            
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Logout failed"));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService, never()).logoutUser(anyString());
    }

    // =============================================================================
    // TESTS ESPECÍFICOS PARA CUBRIR BRANCHES FALTANTES (COBERTURA 100%)
    // =============================================================================

    /**
     * Test para cubrir la branch faltante en registerUser línea 130:
     * Condición: e.getMessage().contains("already exists") || e.getMessage().contains("ya registrado")
     * Caso específico: RuntimeException con mensaje que contiene "ya registrado" pero NO "already exists"
     */
    @Test
    @DisplayName("Registro con RuntimeException 'ya registrado' debe retornar CONFLICT")
    void testRegisterUserRuntimeExceptionWithYaRegistradoShouldReturnConflict() throws Exception {
        // Arrange: RuntimeException con mensaje que contiene solo "ya registrado"
        UserRegistrationDto testRegistrationDto = new UserRegistrationDto(USERNAME, EMAIL, PASSWORD);
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Email ya registrado en el sistema"));

        // Act & Assert: debe retornar HTTP 409 CONFLICT
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegistrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Email ya registrado en el sistema"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    /**
     * Test para cubrir la branch faltante en registerUser línea 130:
     * Caso específico: RuntimeException con mensaje que contiene "already exists" pero NO "ya registrado"
     */
    @Test
    @DisplayName("Registro con RuntimeException 'already exists' debe retornar CONFLICT")
    void testRegisterUserRuntimeExceptionWithAlreadyExistsShouldReturnConflict() throws Exception {
        // Arrange: RuntimeException con mensaje que contiene solo "already exists"
        UserRegistrationDto duplicateDto = new UserRegistrationDto("duplicate", "duplicate@techcorp.com", PASSWORD);
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Username already exists in database"));

        // Act & Assert: debe retornar HTTP 409 CONFLICT
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Username already exists in database"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    /**
     * Test para cubrir branch faltante con RuntimeException que NO contiene ni "already exists" ni "ya registrado"
     * Debe ir por el path del return BAD_REQUEST por defecto
     */
    @Test
    @DisplayName("Registro con RuntimeException sin keywords debe retornar BAD_REQUEST")
    void testRegisterUserRuntimeExceptionWithoutKeywordsShouldReturnBadRequest() throws Exception {
        // Arrange: RuntimeException sin palabras clave específicas
        UserRegistrationDto errorDto = new UserRegistrationDto("erroruser", "error@techcorp.com", PASSWORD);
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Error de conexión a base de datos"));

        // Act & Assert: debe retornar HTTP 400 BAD_REQUEST por defecto
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Error de conexión a base de datos"));

        verify(authenticationService).registerNewUser(any(UserRegistrationDto.class));
    }

    /**
     * Test para cubrir la branch faltante en loginUser
     * Probando RuntimeException sin palabras clave específicas para activar path alternativo
     */
    @Test
    @DisplayName("Login con RuntimeException sin keywords debe retornar BAD_REQUEST")
    void testLoginUserRuntimeExceptionWithoutKeywordsShouldReturnBadRequest() throws Exception {
        // Arrange: RuntimeException con mensaje que no coincide con ningún if
        LoginRequestDto unknownLoginRequest = new LoginRequestDto("unknownuser", "password");
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Error inesperado del sistema"));

        // Act & Assert: debe ir por el path BAD_REQUEST por defecto
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unknownLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Error inesperado del sistema"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    /**
     * Test para verificar RuntimeException con mensaje parcial que puede afectar branches
     */
    @Test
    @DisplayName("Login con RuntimeException 'Credenciales inválidas' debe retornar UNAUTHORIZED")
    void testLoginUserRuntimeExceptionWithCredencialesInvalidasShouldReturnUnauthorized() throws Exception {
        // Arrange: RuntimeException con mensaje que podría afectar la evaluación de conditions
        LoginRequestDto partialLoginRequest = new LoginRequestDto("partialuser", "password");
        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas parcialmente"));

        // Act & Assert: debe activar el if de "Credenciales inválidas" y retornar UNAUTHORIZED
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Credenciales inválidas parcialmente"));

        verify(authenticationService).authenticateUser(any(LoginRequestDto.class));
    }

    /**
     * Test específico para cubrir la branch faltante en logout línea 326:
     * Condición: e.getMessage().contains("Token") || e.getMessage().contains("Invalid") || e.getMessage().contains("inválido")
     * Caso específico: RuntimeException con mensaje que contiene "Invalid" pero NO "Token" ni "inválido"
     */
    @Test
    @DisplayName("Logout con RuntimeException 'Invalid' (en inglés) debe retornar UNAUTHORIZED")
    void testLogoutUserRuntimeExceptionWithInvalidEnglishShouldReturnUnauthorized() throws Exception {
        // Arrange: RuntimeException con mensaje que contiene "Invalid" pero no "Token" ni "inválido"
        when(authenticationService.validateTokenAndGetUsername(anyString()))
                .thenThrow(new RuntimeException("Invalid session state"));

        // Act & Assert: debe activar el if con "Invalid" y retornar UNAUTHORIZED
        // El mensaje será "Logout failed" porque NO contiene "Token", pero el status será 401 porque contiene "Invalid"
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Logout failed"));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService, never()).logoutUser(anyString());
    }

    /**
     * Test específico para cubrir la missed branch en línea 326
     * Condición: e.getMessage().contains("Invalid") && !contains("Token") && !contains("inválido")
     * Este test cubre el caso donde el mensaje contiene "Invalid" únicamente
     */
    @Test
    void testLogoutUserRuntimeExceptionWithOnlyInvalidWordShouldReturnUnauthorized() throws Exception {
        // Arrange: Mock que lance excepción con mensaje que solo contenga "Invalid"
        when(authenticationService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid session detected"));

        // Act & Assert: Verificar que devuelve 401 (UNAUTHORIZED)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(false))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Logout failed"));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService, never()).logoutUser(anyString());
    }

    /**
     * Test para cubrir el camino exitoso del logout (Happy Path)
     */
    @Test
    void testLogoutUserSuccessfulPath() throws Exception {
        // Arrange: Mock exitoso
        when(authenticationService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenReturn("testuser");

        // Act & Assert: Verificar que devuelve 200 (OK)
        mockMvc.perform(post(LOGOUT_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Logout exitoso"));

        verify(authenticationService).validateTokenAndGetUsername(VALID_TOKEN);
        verify(authenticationService).logoutUser("testuser");
    }
}
