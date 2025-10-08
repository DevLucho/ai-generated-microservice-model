package com.techcorp.authapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el registro de nuevos usuarios en el sistema TechCorp
 * Contiene las validaciones necesarias para garantizar la integridad de los datos
 */
@Schema(description = "Datos requeridos para el registro de un nuevo usuario")
public class UserRegistrationDto {
    
    @Schema(
        description = "Nombre de usuario único en el sistema",
        example = "juan.perez",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 3,
        maxLength = 50
    )
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;
    
    @Schema(
        description = "Contraseña del usuario (mínimo 6 caracteres)",
        example = "password123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 6,
        format = "password"
    )
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @Schema(
        description = "Dirección de correo electrónico válida del usuario",
        example = "juan.perez@TechCorp.com",
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "email"
    )
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email es inválido")
    private String emailAddress;
    
    // Constructor por defecto
    public UserRegistrationDto() {}
    
    // Constructor completo
    public UserRegistrationDto(String username, String password, String emailAddress) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
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
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    @Override
    public String toString() {
        return "UserRegistrationDto{" +
                "username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
