package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.config.TestSecurityConfig;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.repository.InMemoryUserRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para UserManagementController para mejorar cobertura de código
 * Clase con cobertura inicial del 2% - necesita tests completos
 */
@WebMvcTest(UserManagementController.class)
@Import(TestSecurityConfig.class)
@DisplayName("UserManagementController - Tests de Cobertura")
class UserManagementControllerTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String API_USERS_ENDPOINT = "/api/users";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_DATA_PATH = "$.data";
    private static final String JSON_TIMESTAMP_PATH = "$.timestamp";
    private static final String DEACTIVATE_ENDPOINT = "/deactivate";
    
    // Test data constants
    private static final String TEST_USERNAME = "test.user";
    private static final String TEST_EMAIL = "test.user@techcorp.com";
    private static final String TEST_USER_ID = "USR-12345";
    private static final String NONEXISTENT_USERNAME = "nonexistent.user";
    
    // Success messages
    private static final String SUCCESS_MESSAGE_USERS_RETRIEVED = "Users retrieved successfully";
    private static final String SUCCESS_MESSAGE_USER_FOUND = "User found successfully";
    private static final String SUCCESS_MESSAGE_USER_DEACTIVATED = "User account deactivated successfully";
    
    // Error messages
    private static final String ERROR_MESSAGE_USER_NOT_FOUND = "User not found";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SystemUser testUser;
    private List<SystemUser> testUsersList;

    @BeforeEach
    void setUp() {
        // Arrange: Configurar datos de prueba
        testUser = new SystemUser();
        testUser.setUserId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmailAddress(TEST_EMAIL);
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setAccountActive(true);

        SystemUser testUser2 = new SystemUser();
        testUser2.setUserId("USR-67890");
        testUser2.setUsername("maria.gonzalez");
        testUser2.setEmailAddress("maria.gonzalez@techcorp.com");
        testUser2.setRegistrationDate(LocalDateTime.now());
        testUser2.setAccountActive(true);

        testUsersList = Arrays.asList(testUser, testUser2);
    }

    @Test
    @DisplayName("GET /api/users - Debe retornar lista de usuarios exitosamente")
    @WithMockUser(roles = "USER")
    void testGetAllUsersWhenUsersExistReturnsUsersList() throws Exception {
        // Arrange
        when(userRepository.findAllUsers()).thenReturn(testUsersList);

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USERS_RETRIEVED))
                .andExpect(jsonPath(JSON_DATA_PATH).isArray())
                .andExpect(jsonPath("$.data[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data[0].username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data[0].emailAddress").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data[0].accountActive").value(true))
                .andExpect(jsonPath("$.data[1].username").value("maria.gonzalez"))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - Debe manejar excepción en repositorio")
    @WithMockUser(roles = "USER")
    void testGetAllUsersWhenRepositoryThrowsExceptionReturnsInternalServerError() throws Exception {
        // Arrange
        when(userRepository.findAllUsers()).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error retrieving users: Database connection error"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("GET /api/users/{username} - Debe retornar usuario cuando existe")
    @WithMockUser(roles = "USER")
    void testGetUserByUsernameWhenUserExistsReturnsUser() throws Exception {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT + "/" + TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USER_FOUND))
                .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.emailAddress").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.accountActive").value(true))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    @DisplayName("GET /api/users/{username} - Debe retornar 404 cuando usuario no existe")
    @WithMockUser(roles = "USER")
    void testGetUserByUsernameWhenUserNotFoundReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.findByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT + "/" + NONEXISTENT_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(ERROR_MESSAGE_USER_NOT_FOUND))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(NONEXISTENT_USERNAME);
    }

    @Test
    @DisplayName("GET /api/users/{username} - Debe manejar excepción en búsqueda")
    @WithMockUser(roles = "USER")
    void testGetUserByUsernameWhenRepositoryThrowsExceptionReturnsInternalServerError() throws Exception {
        // Arrange
        when(userRepository.findByUsername(anyString()))
                .thenThrow(new RuntimeException("Database query failed"));

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT + "/" + TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error retrieving user: Database query failed"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    @DisplayName("PUT /api/users/{username}/deactivate - Debe desactivar usuario existente")
    @WithMockUser(roles = "ADMIN")
    void testDeactivateUserWhenUserExistsDeactivatesSuccessfully() throws Exception {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userRepository.updateUser(any(SystemUser.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(put(API_USERS_ENDPOINT + "/" + TEST_USERNAME + DEACTIVATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USER_DEACTIVATED))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, times(1)).updateUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("PUT /api/users/{username}/deactivate - Debe retornar 404 para usuario inexistente")
    @WithMockUser(roles = "ADMIN")
    void testDeactivateUserWhenUserNotFoundReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.findByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put(API_USERS_ENDPOINT + "/" + NONEXISTENT_USERNAME + DEACTIVATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(ERROR_MESSAGE_USER_NOT_FOUND))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(NONEXISTENT_USERNAME);
        verify(userRepository, never()).updateUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("PUT /api/users/{username}/deactivate - Debe manejar excepción en actualización")
    @WithMockUser(roles = "ADMIN")
    void testDeactivateUserWhenRepositoryThrowsExceptionReturnsInternalServerError() throws Exception {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Update operation failed"))
                .when(userRepository).updateUser(any(SystemUser.class));

        // Act & Assert
        mockMvc.perform(put(API_USERS_ENDPOINT + "/" + TEST_USERNAME + DEACTIVATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error deactivating user: Update operation failed"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, times(1)).updateUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("GET /api/users - Debe retornar lista vacía cuando no hay usuarios")
    @WithMockUser(roles = "USER")
    void testGetAllUsersWhenNoUsersExistReturnsEmptyList() throws Exception {
        // Arrange
        when(userRepository.findAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get(API_USERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USERS_RETRIEVED))
                .andExpect(jsonPath(JSON_DATA_PATH).isArray())
                .andExpect(jsonPath(JSON_DATA_PATH).isEmpty())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - Repository exception with detailed error message")
    void testGetAllUsersWithDetailedExceptionMessage() throws Exception {
        // Arrange: se configura el mock para lanzar una excepción específica
        when(userRepository.findAllUsers()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert: se verifica que se maneja correctamente la excepción específica
        mockMvc.perform(get(API_USERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error retrieving users: Database connection failed"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("GET /api/users/{username} - Repository exception with detailed error message")
    void testGetUserByUsernameWithDetailedExceptionMessage() throws Exception {
        // Arrange: se configura el mock para lanzar una excepción específica
        when(userRepository.findByUsername(TEST_USERNAME)).thenThrow(new RuntimeException("Database timeout"));

        // Act & Assert: se verifica que se maneja correctamente la excepción específica
        mockMvc.perform(get(API_USERS_ENDPOINT + "/" + TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error retrieving user: Database timeout"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    @DisplayName("PUT /api/users/{username}/deactivate - Repository exception with detailed error message")
    void testDeactivateUserWithDetailedExceptionMessage() throws Exception {
        // Arrange: se configura el mock para encontrar usuario pero fallar en update
        SystemUser userToDeactivate = testUser;
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(userToDeactivate));
        when(userRepository.updateUser(any(SystemUser.class))).thenThrow(new RuntimeException("Update operation failed"));

        // Act & Assert: se verifica que se maneja correctamente la excepción específica
        mockMvc.perform(put(API_USERS_ENDPOINT + "/" + TEST_USERNAME + DEACTIVATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(false))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value("Error deactivating user: Update operation failed"))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, times(1)).updateUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("PUT /api/users/{username}/deactivate - Verify user state changes correctly")
    void testDeactivateUserVerifyStateChange() throws Exception {
        // Arrange: se crea un usuario activo
        SystemUser activeUser = new SystemUser();
        activeUser.setUserId(TEST_USER_ID);
        activeUser.setUsername(TEST_USERNAME);
        activeUser.setEmailAddress(TEST_EMAIL);
        activeUser.setAccountActive(true); // Explicitly set to active
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(activeUser));
        when(userRepository.updateUser(any(SystemUser.class))).thenReturn(activeUser);

        // Act: se desactiva el usuario
        mockMvc.perform(put(API_USERS_ENDPOINT + "/" + TEST_USERNAME + DEACTIVATE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USER_DEACTIVATED))
                .andExpect(jsonPath(JSON_DATA_PATH).doesNotExist())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        // Assert: se verifica que el usuario fue marcado como inactivo
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(userRepository, times(1)).updateUser(argThat(user -> !user.isAccountActive()));
    }
}