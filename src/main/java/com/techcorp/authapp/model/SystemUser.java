package com.techcorp.authapp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

/**
 * Entidad SystemUser que representa un usuario registrado en el sistema TechCorp
 * Contiene toda la información necesaria para la gestión de usuarios
 */
@Schema(description = "Modelo de usuario del sistema TechCorp")
public class SystemUser {
    
    @Schema(
        description = "Identificador único del usuario generado automáticamente",
        example = "USR-12345",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String userId;
    
    @Schema(
        description = "Nombre de usuario único en el sistema",
        example = "juan.perez",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
    
    @Schema(hidden = true)
    @JsonIgnore
    private String encodedPassword;
    
    @Schema(
        description = "Dirección de correo electrónico del usuario",
        example = "juan.perez@TechCorp.com",
        format = "email"
    )
    private String emailAddress;
    
    @Schema(
        description = "Fecha y hora de registro del usuario",
        example = "2024-01-15T10:30:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime registrationDate;
    
    @Schema(
        description = "Indica si la cuenta del usuario está activa",
        example = "true",
        defaultValue = "true"
    )
    private boolean accountActive;
    
    /**
     * Constructor por defecto
     */
    public SystemUser() {
        this.registrationDate = LocalDateTime.now();
        this.accountActive = true;
    }
    
    /**
     * Constructor completo para crear un nuevo usuario
     */
    public SystemUser(String userId, String username, String encodedPassword, String emailAddress) {
        this();
        this.userId = userId;
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.emailAddress = emailAddress;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEncodedPassword() {
        return encodedPassword;
    }
    
    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public boolean isAccountActive() {
        return accountActive;
    }
    
    public void setAccountActive(boolean accountActive) {
        this.accountActive = accountActive;
    }
    
    @Override
    public String toString() {
        return "SystemUser{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", registrationDate=" + registrationDate +
                ", accountActive=" + accountActive +
                '}';
    }
}
