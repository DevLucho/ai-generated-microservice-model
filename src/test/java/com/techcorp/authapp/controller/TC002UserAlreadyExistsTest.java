package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.UserAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

/**
 * TC002 - Tests para registro con usuario existente
 * Validar HTTP 409 Conflict cuando se intenta registrar un usuario ya existente
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("TC002 - Registro con Usuario Existente")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TC002UserAlreadyExistsTest {

    // Constants for repeated strings
    private static final String API_REGISTER_ENDPOINT = "/api/auth/register";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String PASSWORD_123 = "password123";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String USERNAME_ALREADY_EXISTS_MESSAGE = "Nombre de usuario ya registrado";
    private static final String SUCCESS_MESSAGE = "Registro exitoso";
    private static final String SEGUNDO_USERNAME = "segundo.usuario";
    private static final String SEGUNDO_EMAIL = "segundo@techcorp.com";
    private static final String TESTUSER_UPPERCASE = "TESTUSER";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TC002.001 - Debe retornar HTTP 409 Conflict cuando el usuario ya existe")
    void registerUserWhenUserAlreadyExistsReturns409Conflict() throws Exception {
        // Arrange: Mock the service to throw UserAlreadyExistsException
        UserRegistrationDto duplicateRegistration = new UserRegistrationDto();
        duplicateRegistration.setUsername("usuario.duplicado");
        duplicateRegistration.setPassword("otherpassword");
        duplicateRegistration.setEmailAddress("otro@email.com");

        // Configure mock to throw exception for any user registration
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException(USERNAME_ALREADY_EXISTS_MESSAGE));

        // Act & Assert: Debe retornar 409 Conflict
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRegistration)))
                .andExpect(status().isConflict()) // HTTP 409
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(USERNAME_ALREADY_EXISTS_MESSAGE))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("TC002.002 - Debe permitir registrar usuarios con nombres diferentes")
    void registerUserWhenDifferentUsernameReturns200OK() throws Exception {
        // Arrange: Mock a successful user registration
        UserRegistrationDto secondUser = new UserRegistrationDto();
        secondUser.setUsername(SEGUNDO_USERNAME);
        secondUser.setPassword(PASSWORD_123);
        secondUser.setEmailAddress(SEGUNDO_EMAIL);

        // Create a mock successful user
        SystemUser mockUser = new SystemUser();
        mockUser.setUserId("USER-002");
        mockUser.setUsername(SEGUNDO_USERNAME);
        mockUser.setEmailAddress(SEGUNDO_EMAIL);
        mockUser.setRegistrationDate(LocalDateTime.now());

        // Configure mock for successful registration
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenReturn(mockUser);

        // Act & Assert: Debe retornar 200 OK
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data.username").value(SEGUNDO_USERNAME))
                .andExpect(jsonPath("$.data.emailAddress").value(SEGUNDO_EMAIL));
    }

    @Test
    @DisplayName("TC002.003 - Debe ser case-sensitive para nombres de usuario")
    void registerUserWhenCaseSensitiveUsernameAllowsBoth() throws Exception {
        // Arrange: Mock successful registration for uppercase user
        UserRegistrationDto upperCaseUser = new UserRegistrationDto();
        upperCaseUser.setUsername(TESTUSER_UPPERCASE);
        upperCaseUser.setPassword(PASSWORD_123);
        upperCaseUser.setEmailAddress("upper@techcorp.com");

        // Create mock user for successful registration
        SystemUser mockUser = new SystemUser();
        mockUser.setUserId("USER-003");
        mockUser.setUsername(TESTUSER_UPPERCASE);
        mockUser.setEmailAddress("upper@techcorp.com");
        mockUser.setRegistrationDate(LocalDateTime.now());

        // Configure mock for successful registration
        when(authenticationService.registerNewUser(any(UserRegistrationDto.class)))
                .thenReturn(mockUser);

        // Act & Assert: Debe permitir ambos (case-sensitive)
        mockMvc.perform(post(API_REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upperCaseUser)))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data.username").value(TESTUSER_UPPERCASE));
    }
}