package com.techcorp.authapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO especializado para respuestas de error con información detallada
 * Proporciona estructura estándar para manejo de errores en la API TechCorp
 */
@Schema(description = "Respuesta de error detallada de la API TechCorp")
public class ErrorResponseDto {
    
    @Schema(
        description = "Código de estado HTTP del error",
        example = "400"
    )
    private int statusCode;
    
    @Schema(
        description = "Mensaje de error principal",
        example = "Validation failed"
    )
    private String error;
    
    @Schema(
        description = "Descripción detallada del error",
        example = "Username must be between 3 and 50 characters"
    )
    private String message;
    
    @Schema(
        description = "Ruta del endpoint donde ocurrió el error",
        example = "/api/auth/register"
    )
    private String path;
    
    @Schema(
        description = "Timestamp del error",
        example = "2024-01-15T12:00:00"
    )
    private LocalDateTime timestamp;
    
    @Schema(
        description = "Lista de errores de validación específicos"
    )
    private List<FieldErrorDto> fieldErrors;
    
    @Schema(
        description = "ID único del error para trazabilidad",
        example = "ERR-20240115-001"
    )
    private String errorId;
    
    // Constructor por defecto
    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor básico
    public ErrorResponseDto(int statusCode, String error, String message, String path) {
        this();
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    // Constructor completo
    public ErrorResponseDto(int statusCode, String error, String message, String path, 
                           List<FieldErrorDto> fieldErrors, String errorId) {
        this(statusCode, error, message, path);
        this.fieldErrors = fieldErrors;
        this.errorId = errorId;
    }
    
    // Getters y Setters
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<FieldErrorDto> getFieldErrors() {
        return fieldErrors;
    }
    
    public void setFieldErrors(List<FieldErrorDto> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public String getErrorId() {
        return errorId;
    }
    
    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }
    
    @Override
    public String toString() {
        return "ErrorResponseDto{" +
                "statusCode=" + statusCode +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", errorId='" + errorId + '\'' +
                '}';
    }
    
    /**
     * Clase interna para errores de campo específicos
     */
    @Schema(description = "Error de validación específico de un campo")
    public static class FieldErrorDto {
        
        @Schema(description = "Nombre del campo con error", example = "username")
        private String field;
        
        @Schema(description = "Valor rechazado", example = "us")
        private Object rejectedValue;
        
        @Schema(description = "Mensaje de error del campo", example = "Username must be between 3 and 50 characters")
        private String message;
        
        public FieldErrorDto() {}
        
        public FieldErrorDto(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
        
        public Object getRejectedValue() {
            return rejectedValue;
        }
        
        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "FieldErrorDto{" +
                    "field='" + field + '\'' +
                    ", rejectedValue=" + rejectedValue +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
