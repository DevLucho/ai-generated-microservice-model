package com.techcorp.authapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para SystemController para mejorar cobertura de código
 * Clase con cobertura inicial del 1.5% - necesita tests completos
 */
@WebMvcTest(SystemController.class)
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "spring.application.name=user-management-service",
    "app.version=1.0.0"
})
@DisplayName("SystemController - Tests de Cobertura")
class SystemControllerTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String API_SYSTEM_ENDPOINT = "/api/system";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String JSON_SUCCESS_PATH = "$.success";
    private static final String JSON_MESSAGE_PATH = "$.message";
    private static final String JSON_DATA_PATH = "$.data";
    private static final String JSON_TIMESTAMP_PATH = "$.timestamp";
    
    // Endpoint paths
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String INFO_ENDPOINT = "/info";
    private static final String STATS_ENDPOINT = "/stats";
    private static final String VERSION_ENDPOINT = "/version";
    
    // Expected message constants
    private static final String SUCCESS_MESSAGE_HEALTHY = "Service is healthy and running";
    private static final String SUCCESS_MESSAGE_SYSTEM_INFO = "System information retrieved successfully";
    private static final String SUCCESS_MESSAGE_USER_STATS = "User statistics retrieved successfully";
    private static final String SUCCESS_MESSAGE_VERSION = "API version retrieved successfully";
    
    // Test data constants
    private static final long TOTAL_USERS_COUNT = 100L;
    private static final long ACTIVE_USERS_COUNT = 85L;
    private static final String APPLICATION_NAME = "user-management-service";
    private static final String APPLICATION_VERSION = "1.0.0";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configurar comportamiento por defecto del mock repository
        when(userRepository.countUsers()).thenReturn(TOTAL_USERS_COUNT);
        when(userRepository.countActiveUsers()).thenReturn(ACTIVE_USERS_COUNT);
    }

    @Test
    @DisplayName("GET /api/system/health - Debe retornar estado saludable del servicio")
    void testHealthCheckReturnsHealthyStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + HEALTH_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_HEALTHY))
                .andExpect(jsonPath(JSON_DATA_PATH + ".status").value("UP"))
                .andExpect(jsonPath(JSON_DATA_PATH + ".service").value(APPLICATION_NAME))
                .andExpect(jsonPath(JSON_DATA_PATH + ".version").value(APPLICATION_VERSION))
                .andExpect(jsonPath(JSON_DATA_PATH + ".timestamp").exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("GET /api/system/info - Debe retornar información completa del sistema")
    void testSystemInfoReturnsCompleteSystemInformation() throws Exception {
        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + INFO_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_SYSTEM_INFO))
                .andExpect(jsonPath(JSON_DATA_PATH + ".applicationName").value(APPLICATION_NAME))
                .andExpect(jsonPath(JSON_DATA_PATH + ".version").value(APPLICATION_VERSION))
                .andExpect(jsonPath(JSON_DATA_PATH + ".javaVersion").exists())
                .andExpect(jsonPath(JSON_DATA_PATH + ".springBootVersion").value("3.2.0"))
                .andExpect(jsonPath(JSON_DATA_PATH + ".startTime").exists())
                .andExpect(jsonPath(JSON_DATA_PATH + ".totalUsers").value(TOTAL_USERS_COUNT))
                .andExpect(jsonPath(JSON_DATA_PATH + ".activeUsers").value(ACTIVE_USERS_COUNT))
                .andExpect(jsonPath(JSON_DATA_PATH + ".environment").value("development"))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        // Verificar que se llamaron los métodos del repositorio
        verify(userRepository, times(1)).countUsers();
        verify(userRepository, times(1)).countActiveUsers();
    }

    @Test
    @DisplayName("GET /api/system/stats - Debe retornar estadísticas detalladas de usuarios")
    void testUserStatsReturnsDetailedUserStatistics() throws Exception {
        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + STATS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_USER_STATS))
                .andExpect(jsonPath(JSON_DATA_PATH + ".totalRegisteredUsers").value(TOTAL_USERS_COUNT))
                .andExpect(jsonPath(JSON_DATA_PATH + ".activeUsers").value(ACTIVE_USERS_COUNT))
                .andExpect(jsonPath(JSON_DATA_PATH + ".inactiveUsers").value(TOTAL_USERS_COUNT - ACTIVE_USERS_COUNT))
                .andExpect(jsonPath(JSON_DATA_PATH + ".usersWithActiveTokens").exists())
                .andExpect(jsonPath(JSON_DATA_PATH + ".lastUpdated").exists())
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());

        // Verificar que se llamaron los métodos del repositorio para estadísticas
        verify(userRepository, times(1)).countUsers();
        verify(userRepository, times(2)).countActiveUsers(); // Se llama dos veces: una para activeUsers y otra para calculateActiveTokens
    }

    @Test
    @DisplayName("GET /api/system/version - Debe retornar información de versión de la API")
    void testGetVersionReturnsApiVersionInformation() throws Exception {
        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + VERSION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_CONTENT_TYPE))
                .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                .andExpect(jsonPath(JSON_MESSAGE_PATH).value(SUCCESS_MESSAGE_VERSION))
                .andExpect(jsonPath(JSON_DATA_PATH + ".version").value(APPLICATION_VERSION))
                .andExpect(jsonPath(JSON_DATA_PATH + ".apiName").value("TechCorp User Management API"))
                .andExpect(jsonPath(JSON_DATA_PATH + ".releaseDate").value("2024-01-15"))
                .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
    }

    @Test
    @DisplayName("Múltiples llamadas a health check - Debe ser consistente")
    void testMultipleHealthCheckCallsAreConsistent() throws Exception {
        // Arrange & Act - Llamar health check múltiples veces
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(API_SYSTEM_ENDPOINT + HEALTH_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                    .andExpect(jsonPath(JSON_DATA_PATH + ".status").value("UP"))
                    .andExpect(jsonPath(JSON_DATA_PATH + ".service").value(APPLICATION_NAME));
        }
    }

    @Test
    @DisplayName("Stats con diferentes valores del repositorio - Debe calcular correctamente")
    void testStatsWithDifferentRepositoryValuesCalculatesCorrectly() throws Exception {
        // Arrange - Configurar diferentes valores
        long customTotalUsers = 200L;
        long customActiveUsers = 150L;
        when(userRepository.countUsers()).thenReturn(customTotalUsers);
        when(userRepository.countActiveUsers()).thenReturn(customActiveUsers);

        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + STATS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_DATA_PATH + ".totalRegisteredUsers").value(customTotalUsers))
                .andExpect(jsonPath(JSON_DATA_PATH + ".activeUsers").value(customActiveUsers))
                .andExpect(jsonPath(JSON_DATA_PATH + ".inactiveUsers").value(customTotalUsers - customActiveUsers));

        verify(userRepository, times(1)).countUsers();
        verify(userRepository, times(2)).countActiveUsers();
    }

    @Test
    @DisplayName("Info endpoint con cero usuarios - Debe manejar correctamente")
    void testInfoEndpointWithZeroUsersHandledCorrectly() throws Exception {
        // Arrange - Configurar repositorio sin usuarios
        when(userRepository.countUsers()).thenReturn(0L);
        when(userRepository.countActiveUsers()).thenReturn(0L);

        // Act & Assert
        mockMvc.perform(get(API_SYSTEM_ENDPOINT + INFO_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_DATA_PATH + ".totalUsers").value(0))
                .andExpect(jsonPath(JSON_DATA_PATH + ".activeUsers").value(0))
                .andExpect(jsonPath(JSON_DATA_PATH + ".applicationName").value(APPLICATION_NAME));

        verify(userRepository, times(1)).countUsers();
        verify(userRepository, times(1)).countActiveUsers();
    }

    @Test
    @DisplayName("Todos los endpoints deben tener estructura de respuesta consistente")
    void testAllEndpointsHaveConsistentResponseStructure() throws Exception {
        // Array de endpoints para probar
        String[] endpoints = {HEALTH_ENDPOINT, INFO_ENDPOINT, STATS_ENDPOINT, VERSION_ENDPOINT};

        for (String endpoint : endpoints) {
            mockMvc.perform(get(API_SYSTEM_ENDPOINT + endpoint)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(JSON_CONTENT_TYPE))
                    .andExpect(jsonPath(JSON_SUCCESS_PATH).value(true))
                    .andExpect(jsonPath(JSON_MESSAGE_PATH).exists())
                    .andExpect(jsonPath(JSON_DATA_PATH).exists())
                    .andExpect(jsonPath(JSON_TIMESTAMP_PATH).exists());
        }
    }
}