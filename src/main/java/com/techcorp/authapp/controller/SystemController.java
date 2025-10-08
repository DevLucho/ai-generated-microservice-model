package com.techcorp.authapp.controller;

import com.techcorp.authapp.dto.ApiResponseDto;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para información del sistema y verificación de estado
 * Proporciona endpoints para monitoreo y diagnóstico del servicio
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
@Tag(name = "Sistema", description = "Información del sistema y verificación de estado")
public class SystemController {
    
    @Autowired
    private InMemoryUserRepository userRepository;
    
    @Value("${spring.application.name:user-management-service}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;
    
    /**
     * Verificación de estado del servicio (Health Check)
     */
    @Operation(
        summary = "Verificación de estado del servicio",
        description = "Endpoint para verificar que el servicio está funcionando correctamente",
        tags = {"Sistema"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Servicio funcionando correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Health check exitoso",
                    value = """
                        {
                            "success": true,
                            "message": "Service is healthy and running",
                            "data": {
                                "status": "UP",
                                "service": "user-management-service",
                                "version": "1.0.0",
                                "timestamp": "2024-01-15T12:00:00"
                            },
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", applicationName);
        healthData.put("version", applicationVersion);
        healthData.put("timestamp", LocalDateTime.now());
        
        ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
            true, 
            "Service is healthy and running", 
            healthData
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Información general del sistema
     */
    @Operation(
        summary = "Información del sistema",
        description = "Obtiene información detallada del sistema incluyendo estadísticas básicas",
        tags = {"Sistema"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Información del sistema obtenida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Información del sistema",
                    value = """
                        {
                            "success": true,
                            "message": "System information retrieved successfully",
                            "data": {
                                "applicationName": "user-management-service",
                                "version": "1.0.0",
                                "javaVersion": "17.0.7",
                                "springBootVersion": "3.2.0",
                                "startTime": "2024-01-15T10:00:00",
                                "totalUsers": 150,
                                "activeUsers": 145,
                                "environment": "development"
                            },
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/info")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> systemInfo() {
        Map<String, Object> systemData = new HashMap<>();
        systemData.put("applicationName", applicationName);
        systemData.put("version", applicationVersion);
        systemData.put("javaVersion", System.getProperty("java.version"));
        systemData.put("springBootVersion", "3.2.0");
        systemData.put("startTime", LocalDateTime.now().minusHours(2)); // Simulando tiempo de inicio
        systemData.put("totalUsers", userRepository.countUsers());
        systemData.put("activeUsers", userRepository.countActiveUsers());
        systemData.put("environment", "development");
        
        ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
            true, 
            "System information retrieved successfully", 
            systemData
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Estadísticas de usuarios del sistema
     */
    @Operation(
        summary = "Estadísticas de usuarios",
        description = "Obtiene estadísticas detalladas sobre los usuarios del sistema",
        tags = {"Sistema"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Estadísticas de usuarios",
                    value = """
                        {
                            "success": true,
                            "message": "User statistics retrieved successfully",
                            "data": {
                                "totalRegisteredUsers": 150,
                                "activeUsers": 145,
                                "inactiveUsers": 5,
                                "usersWithActiveTokens": 12,
                                "lastUpdated": "2024-01-15T12:00:00"
                            },
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> userStats() {
        Map<String, Object> statsData = new HashMap<>();
        long totalUsers = userRepository.countUsers();
        long activeUsers = userRepository.countActiveUsers();
        
        statsData.put("totalRegisteredUsers", totalUsers);
        statsData.put("activeUsers", activeUsers);
        statsData.put("inactiveUsers", totalUsers - activeUsers);
        statsData.put("usersWithActiveTokens", calculateActiveTokens());
        statsData.put("lastUpdated", LocalDateTime.now());
        
        ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
            true, 
            "User statistics retrieved successfully", 
            statsData
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Endpoint para obtener la versión de la API
     */
    @Operation(
        summary = "Versión de la API",
        description = "Obtiene la versión actual de la API del servicio",
        tags = {"Sistema"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Versión obtenida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Versión de la API",
                    value = """
                        {
                            "success": true,
                            "message": "API version retrieved successfully",
                            "data": {
                                "version": "1.0.0",
                                "apiName": "TechCorp User Management API",
                                "releaseDate": "2024-01-15"
                            },
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/version")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getVersion() {
        Map<String, Object> versionData = new HashMap<>();
        versionData.put("version", applicationVersion);
        versionData.put("apiName", "TechCorp User Management API");
        versionData.put("releaseDate", "2024-01-15");
        
        ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
            true, 
            "API version retrieved successfully", 
            versionData
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Método helper para calcular tokens activos
     */
    private long calculateActiveTokens() {
        // Simular conteo de tokens activos
        // En una implementación real, esto vendría del repositorio
        return Math.min(userRepository.countActiveUsers(), 15);
    }
}
