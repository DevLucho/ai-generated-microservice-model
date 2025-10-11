package com.techcorp.authapp.service;

/**
 * Excepción personalizada para usuarios no encontrados
 * TC006: Se lanza cuando se intenta autenticar un usuario que no existe
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}