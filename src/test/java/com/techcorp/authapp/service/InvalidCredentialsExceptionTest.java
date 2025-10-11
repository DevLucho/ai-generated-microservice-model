package com.techcorp.authapp.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para InvalidCredentialsException
 * Verifica que los constructores funcionen correctamente
 */
class InvalidCredentialsExceptionTest {

    private static final String TEST_MESSAGE = "Credenciales inválidas";
    private static final String CAUSE_MESSAGE = "Error de autenticación";

    @Test
    void testConstructorWithMessage() {
        // Arrange: se prepara el mensaje de error
        
        // Act: se crea la excepción con mensaje
        InvalidCredentialsException exception = new InvalidCredentialsException(TEST_MESSAGE);
        
        // Assert: se verifica que el mensaje se asigne correctamente
        assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange: se prepara el mensaje y la causa
        Throwable cause = new SecurityException(CAUSE_MESSAGE);
        
        // Act: se crea la excepción con mensaje y causa
        InvalidCredentialsException exception = new InvalidCredentialsException(TEST_MESSAGE, cause);
        
        // Assert: se verifica que el mensaje y la causa se asignen correctamente
        assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo(CAUSE_MESSAGE);
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Arrange: se prepara la excepción
        InvalidCredentialsException exception = new InvalidCredentialsException(TEST_MESSAGE);
        
        // Act & Assert: se verifica que herede de RuntimeException
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void testConstructorWithNullMessage() {
        // Arrange: mensaje nulo
        
        // Act: se crea la excepción con mensaje nulo
        InvalidCredentialsException exception = new InvalidCredentialsException(null);
        
        // Assert: se verifica que acepta mensaje nulo
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testConstructorWithNullMessageAndCause() {
        // Arrange: mensaje y causa nulos
        
        // Act: se crea la excepción con parámetros nulos
        InvalidCredentialsException exception = new InvalidCredentialsException(null, null);
        
        // Assert: se verifica que acepta parámetros nulos
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }
}