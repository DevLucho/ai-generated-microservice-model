package com.techcorp.authapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico para respuestas estandarizadas de la API TechCorp
 * Proporciona una estructura consistente para todas las respuestas del servicio
 */
@Schema(description = "Respuesta estándar de la API TechCorp con formato unificado")
public class ApiResponseDto<T> {
    
    @Schema(
        description = "Indica si la operación fue exitosa",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean success;
    
    @Schema(
        description = "Mensaje descriptivo del resultado de la operación",
        example = "Operación completada exitosamente",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;
    
    @Schema(
        description = "Datos de respuesta de la operación (puede ser null)",
        nullable = true
    )
    private T data;
    
    @Schema(
        description = "Timestamp de la respuesta en formato ISO",
        example = "2024-01-15T10:30:00Z"
    )
    private String timestamp;
    
    // Constructor por defecto
    public ApiResponseDto() {
        this.timestamp = java.time.Instant.now().toString();
    }
    
    // Constructor para respuestas sin datos
    public ApiResponseDto(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    // Constructor para respuestas con datos
    public ApiResponseDto(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ApiResponseDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
