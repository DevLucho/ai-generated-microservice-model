package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.service.AuthenticationService;
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
 * TC004 - Tests para login exitoso
 * Validar HTTP 200 OK cuando se proporcionan credenciales válidas
 */
@WebMvcTest(UserAuthenticationController.class)
@Import(com.techcorp.authapp.config.TestSecurityConfig.class)
@DisplayName("TC004 - Login Exitoso")
class TC004LoginSuccessTest {

    // Constants for repeated strings
    private static final String API_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String SUCCESS_MESSAGE = "Autenticación exitosa";
    private static final String VALID_USERNAME = "test.user";
    private static final String VALID_PASSWORD = "password123";
    private static final String MOCK_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_DATA_PATH = "$.data";
    private static final String JSON_USERNAME_PATH = "$.data.username";
    private static final String JSON_AUTH_TOKEN_PATH = "$.data.authToken";
    private static final String JSON_TOKEN_TYPE_PATH = "$.data.tokenType";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("TC004.1 - Login exitoso debe retornar 200 OK")
    void loginUserWhenValidCredentialsReturns200OK() throws Exception {
        // Arrange
        LoginRequestDto validLogin = new LoginRequestDto();
        validLogin.setUsername(VALID_USERNAME);
        validLogin.setPassword(VALID_PASSWORD);

        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenReturn(MOCK_TOKEN);

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE))
                .andExpect(jsonPath(JSON_DATA_PATH).exists())
                .andExpect(jsonPath(JSON_USERNAME_PATH).value(VALID_USERNAME))
                .andExpect(jsonPath(JSON_AUTH_TOKEN_PATH).value(MOCK_TOKEN))
                .andExpect(jsonPath(JSON_TOKEN_TYPE_PATH).value(BEARER_TOKEN_TYPE));
    }

    @Test
    @DisplayName("TC004.2 - Login exitoso debe incluir información del usuario")
    void loginUserWhenValidCredentialsReturnsUserInformation() throws Exception {
        // Arrange
        LoginRequestDto validLogin = new LoginRequestDto();
        validLogin.setUsername("maria.rodriguez");
        validLogin.setPassword("securePassword456");

        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenReturn(MOCK_TOKEN);

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE))
                .andExpect(jsonPath(JSON_USERNAME_PATH).value("maria.rodriguez"))
                .andExpect(jsonPath(JSON_AUTH_TOKEN_PATH).value(MOCK_TOKEN))
                .andExpect(jsonPath(JSON_TOKEN_TYPE_PATH).value(BEARER_TOKEN_TYPE));
    }

    @Test
    @DisplayName("TC004.3 - Login exitoso debe retornar estructura completa")
    void loginUserWhenValidCredentialsReturnsCompleteStructure() throws Exception {
        // Arrange
        LoginRequestDto validLogin = new LoginRequestDto();
        validLogin.setUsername("juan.perez");
        validLogin.setPassword("myStrongPassword");

        when(authenticationService.authenticateUser(any(LoginRequestDto.class)))
                .thenReturn(MOCK_TOKEN);

        // Act & Assert
        mockMvc.perform(post(API_LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).exists())
                .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                .andExpect(jsonPath(JSON_DATA_PATH).exists())
                .andExpect(jsonPath(JSON_USERNAME_PATH).exists())
                .andExpect(jsonPath(JSON_AUTH_TOKEN_PATH).exists())
                .andExpect(jsonPath(JSON_TOKEN_TYPE_PATH).exists())
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE));
    }
}
