package com.techcorp.authapp.controller;

import com.techcorp.authapp.dto.ApiResponseDto;
import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones de autenticación de usuarios TechCorp
 * Proporciona endpoints para registro, login y logout de usuarios
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Operaciones de autenticación de usuarios (login, registro, logout)")
public class UserAuthenticationController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Registra un nuevo usuario en el sistema TechCorp con validación de datos y generación automática de ID",
        tags = {"Autenticación"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario registrado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Registro exitoso",
                    value = """
                        {
                            "success": true,
                            "message": "Registro exitoso",
                            "data": {
                                "userId": "USR-12345",
                                "username": "juan.perez",
                                "emailAddress": "juan.perez@TechCorp.com",
                                "registrationDate": "2024-01-15T10:30:00Z"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en los datos de registro",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Error de validación",
                    value = """
                        {
                            "success": false,
                            "message": "Username already exists",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> registerUser(
            @Parameter(description = "Datos de registro del nuevo usuario", required = true)
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        
        try {
            SystemUser newUser = authenticationService.registerNewUser(registrationDto);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", newUser.getUserId());
            userData.put("username", newUser.getUsername());
            userData.put("emailAddress", newUser.getEmailAddress());
            userData.put("registrationDate", newUser.getRegistrationDate());
            
            ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
                true, 
                "Registro exitoso", 
                userData
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            ApiResponseDto<Map<String, Object>> errorResponse = new ApiResponseDto<>(
                false, 
                e.getMessage()
            );
            
            // TC002: Manejar usuario duplicado con HTTP 409 Conflict
            if (e.getMessage().contains("already exists") || e.getMessage().contains("ya registrado")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Autentica las credenciales de un usuario
     */
    @Operation(
        summary = "Autenticar usuario",
        description = "Autentica las credenciales de un usuario y genera un token JWT para acceso autorizado",
        tags = {"Autenticación"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Login exitoso",
                    value = """
                        {
                            "success": true,
                            "message": "Login successful",
                            "data": {
                                "username": "juan.perez",
                                "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                "tokenType": "Bearer"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Error de autenticación",
                    value = """
                        {
                            "success": false,
                            "message": "Invalid credentials",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> loginUser(
            @Parameter(description = "Credenciales de acceso del usuario", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        
        try {
            String authToken = authenticationService.authenticateUser(loginRequest);
            
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("username", loginRequest.getUsername());
            loginData.put("authToken", authToken);
            loginData.put("tokenType", "Bearer");
            
            ApiResponseDto<Map<String, Object>> response = new ApiResponseDto<>(
                true, 
                "Autenticación exitosa", 
                loginData
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            ApiResponseDto<Map<String, Object>> errorResponse = new ApiResponseDto<>(
                false, 
                e.getMessage()
            );
            
            // TC006: Usuario no encontrado - HTTP 404
            if (e.getMessage().contains("User not found") || e.getMessage().contains("Usuario no encontrado")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            
            // TC005: Credenciales inválidas - HTTP 401  
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Cierra la sesión de un usuario
     */
    @Operation(
        summary = "Cerrar sesión de usuario",
        description = "Cierra la sesión de un usuario invalidando su token de acceso",
        tags = {"Autenticación"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout exitoso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "Logout exitoso",
                    value = """
                        {
                            "success": true,
                            "message": "Logout successful",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en el logout",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponseDto.class)
            )
        )
    })
    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponseDto<Void>> logoutUser(
            @Parameter(description = "Token JWT en el header Authorization", required = true)
            @RequestHeader("Authorization") String authorizationHeader) {
        
        try {
            // TC009: Validar que el header Authorization esté presente
            if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
                ApiResponseDto<Void> errorResponse = new ApiResponseDto<>(
                    false, 
                    "Token requerido"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // TC008: Validar formato del token Bearer
            if (!authorizationHeader.startsWith("Bearer ")) {
                ApiResponseDto<Void> errorResponse = new ApiResponseDto<>(
                    false, 
                    "Token inválido"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            
            String token = authorizationHeader.substring(7); // Remover "Bearer "
            
            // TC008: Validar token y obtener username
            String username = authenticationService.validateTokenAndGetUsername(token);
            
            authenticationService.logoutUser(username);
            
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                true, 
                "Logout exitoso"
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            ApiResponseDto<Void> errorResponse = new ApiResponseDto<>(
                false, 
                e.getMessage().contains("Token") ? "Token inválido" : "Logout failed"
            );
            
            // TC008: Token inválido - HTTP 401
            if (e.getMessage().contains("Token") || e.getMessage().contains("Invalid") || e.getMessage().contains("inválido")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
