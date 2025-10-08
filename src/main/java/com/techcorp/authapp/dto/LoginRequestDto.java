package com.techcorp.authapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de autenticación de usuarios
 * Contiene las credenciales necesarias para el login en el sistema TechCorp
 */
@Schema(description = "Credenciales de acceso para autenticación de usuario")
public class LoginRequestDto {
    
    @Schema(
        description = "Nombre de usuario registrado en el sistema",
        example = "juan.perez",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(
        description = "Contraseña del usuario",
        example = "password123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "password"
    )
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructor por defecto
    public LoginRequestDto() {}
    
    // Constructor completo
    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "LoginRequestDto{" +
                "username='" + username + '\'' +
                '}';
    }
}
