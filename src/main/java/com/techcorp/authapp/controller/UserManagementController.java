package com.techcorp.authapp.controller;

import com.techcorp.authapp.dto.ApiResponseDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para operaciones de gestión de usuarios TechCorp
 * Proporciona endpoints para consultar y administrar usuarios del sistema
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Gestión de Usuarios", description = "Operaciones de gestión y administración de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class UserManagementController {
    
    @Autowired
    private InMemoryUserRepository userRepository;
    
    /**
     * Obtiene la lista de todos los usuarios registrados
     */
    @Operation(
        summary = "Listar todos los usuarios",
        description = "Obtiene una lista completa de todos los usuarios registrados en el sistema TechCorp",
        tags = {"Gestión de Usuarios"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Lista de usuarios",
                    value = """
                        {
                            "success": true,
                            "message": "Users retrieved successfully",
                            "data": [
                                {
                                    "userId": "USR-12345",
                                    "username": "juan.perez",
                                    "emailAddress": "juan.perez@TechCorp.com",
                                    "registrationDate": "2024-01-15T10:30:00",
                                    "accountActive": true
                                }
                            ],
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token de autenticación requerido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<SystemUser>>> getAllUsers() {
        try {
            List<SystemUser> users = userRepository.findAllUsers();
            
            ApiResponseDto<List<SystemUser>> response = new ApiResponseDto<>(
                true, 
                "Users retrieved successfully", 
                users
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            ApiResponseDto<List<SystemUser>> errorResponse = new ApiResponseDto<>(
                false, 
                "Error retrieving users: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Obtiene un usuario específico por su nombre de usuario
     */
    @Operation(
        summary = "Obtener usuario por username",
        description = "Busca y retorna la información de un usuario específico utilizando su nombre de usuario",
        tags = {"Gestión de Usuarios"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Usuario encontrado",
                    value = """
                        {
                            "success": true,
                            "message": "User found successfully",
                            "data": {
                                "userId": "USR-12345",
                                "username": "juan.perez",
                                "emailAddress": "juan.perez@TechCorp.com",
                                "registrationDate": "2024-01-15T10:30:00",
                                "accountActive": true
                            },
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Usuario no encontrado",
                    value = """
                        {
                            "success": false,
                            "message": "User not found",
                            "data": null,
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token de autenticación requerido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class)
            )
        )
    })
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponseDto<SystemUser>> getUserByUsername(
            @Parameter(description = "Nombre de usuario a buscar", required = true, example = "juan.perez")
            @PathVariable String username) {
        
        try {
            Optional<SystemUser> user = userRepository.findByUsername(username);
            
            if (user.isPresent()) {
                ApiResponseDto<SystemUser> response = new ApiResponseDto<>(
                    true, 
                    "User found successfully", 
                    user.get()
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ApiResponseDto<SystemUser> response = new ApiResponseDto<>(
                    false, 
                    "User not found"
                );
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            ApiResponseDto<SystemUser> errorResponse = new ApiResponseDto<>(
                false, 
                "Error retrieving user: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Desactiva una cuenta de usuario
     */
    @Operation(
        summary = "Desactivar cuenta de usuario",
        description = "Desactiva la cuenta de un usuario específico sin eliminar sus datos del sistema",
        tags = {"Gestión de Usuarios"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cuenta desactivada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Cuenta desactivada",
                    value = """
                        {
                            "success": true,
                            "message": "User account deactivated successfully",
                            "data": null,
                            "timestamp": "2024-01-15T12:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token de autenticación requerido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class)
            )
        )
    })
    @PutMapping("/{username}/deactivate")
    public ResponseEntity<ApiResponseDto<Void>> deactivateUser(
            @Parameter(description = "Nombre de usuario a desactivar", required = true, example = "juan.perez")
            @PathVariable String username) {
        
        try {
            Optional<SystemUser> user = userRepository.findByUsername(username);
            
            if (user.isPresent()) {
                SystemUser existingUser = user.get();
                existingUser.setAccountActive(false);
                userRepository.updateUser(existingUser);
                
                ApiResponseDto<Void> response = new ApiResponseDto<>(
                    true, 
                    "User account deactivated successfully"
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ApiResponseDto<Void> response = new ApiResponseDto<>(
                    false, 
                    "User not found"
                );
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            ApiResponseDto<Void> errorResponse = new ApiResponseDto<>(
                false, 
                "Error deactivating user: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
