package com.techcorp.authapp.service;

/**
 * Excepción lanzada cuando se intenta registrar un usuario con un nombre de usuario ya existente
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}