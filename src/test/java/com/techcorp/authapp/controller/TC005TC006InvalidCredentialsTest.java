package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
import com.techcorp.authapp.service.InvalidCredentialsException;
import com.techcorp.authapp.service.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TC005/TC006 - Tests para credenciales inválidas en login
 * Validar HTTP 401 Unauthorized cuando se proporcionan credenciales incorrectas
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(com.techcorp.authapp.config.TestSecurityConfig.class)
@DisplayName("TC005/TC006 - Credenciales Inválidas")
class TC005TC006InvalidCredentialsTest {

    // Constants for repeated strings
    private static final String API_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_DATA_PATH = "$.data";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("TC005 - Password incorrecto debe retornar 401")
    void loginUserWhenIncorrectPasswordReturns401Unauthorized() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("user.existente");
        loginRequest.setPassword("passwordIncorrecto");

        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // HTTP 401
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(INVALID_CREDENTIALS_MESSAGE))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist());
    }

    @Test
    @DisplayName("TC006 - Usuario inexistente debe retornar 401")
    void loginUserWhenUserNotFoundReturns401Unauthorized() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("usuario.inexistente");
        loginRequest.setPassword("cualquierPassword");

        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // HTTP 401
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(USER_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist());
    }

    @Test
    @DisplayName("TC005.1 - Diferentes passwords incorrectos deben retornar 401")
    void loginUserWhenVariousIncorrectPasswordsReturn401() throws Exception {
        // Arrange
        String[] incorrectPasswords = {
            "password_incorrecta",
            "12345",
            "admin",
            "contraseña_falsa",
            "password_equivocada"
        };

        for (String password : incorrectPasswords) {
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername("usuario.test");
            loginRequest.setPassword(password);

            when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                    .thenThrow(new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

            // Act & Assert
            mockMvc.perform(post(API_LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized()) // HTTP 401
                    .andExpect(content().contentType(JSON_CONTENT_TYPE))
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                    .andExpect(jsonPath(JSON_MESSAGE_PATH).value(INVALID_CREDENTIALS_MESSAGE));
        }
    }

    @Test
    @DisplayName("TC006.1 - Diferentes usuarios inexistentes deben retornar 401")
    void loginUserWhenVariousNonExistentUsersReturn401() throws Exception {
        // Arrange
        String[] nonExistentUsers = {
            "admin",
            "root",
            "usuario.falso",
            "hacker",
            "guest"
        };

        for (String username : nonExistentUsers) {
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setUsername(username);
            loginRequest.setPassword("cualquierPassword");

            when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                    .thenThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

            // Act & Assert
            mockMvc.perform(post(API_LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized()) // HTTP 401
                    .andExpect(content().contentType(JSON_CONTENT_TYPE))
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                    .andExpect(jsonPath(JSON_MESSAGE_PATH).value(USER_NOT_FOUND_MESSAGE));
        }
    }
}