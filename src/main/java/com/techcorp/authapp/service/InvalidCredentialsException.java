package com.techcorp.authapp.service;

/**
 * Excepción personalizada para credenciales inválidas
 * TC005: Se lanza cuando la contraseña no coincide con la almacenada
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}