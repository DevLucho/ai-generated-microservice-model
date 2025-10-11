package com.techcorp.authapp.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para UserAlreadyExistsException
 * Verifica que los constructores funcionen correctamente
 */
class UserAlreadyExistsExceptionTest {

    private static final String TEST_MESSAGE = "Usuario ya existe";
    private static final String CAUSE_MESSAGE = "Error de validación";

    @Test
    void testConstructorWithMessage() {
        // Arrange: se prepara el mensaje de error
        
        // Act: se crea la excepción con mensaje
        UserAlreadyExistsException exception = new UserAlreadyExistsException(TEST_MESSAGE);
        
        // Assert: se verifica que el mensaje se asigne correctamente
        assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange: se prepara el mensaje y la causa
        Throwable cause = new IllegalArgumentException(CAUSE_MESSAGE);
        
        // Act: se crea la excepción con mensaje y causa
        UserAlreadyExistsException exception = new UserAlreadyExistsException(TEST_MESSAGE, cause);
        
        // Assert: se verifica que el mensaje y la causa se asignen correctamente
        assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo(CAUSE_MESSAGE);
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Arrange: se prepara la excepción
        UserAlreadyExistsException exception = new UserAlreadyExistsException(TEST_MESSAGE);
        
        // Act & Assert: se verifica que herede de RuntimeException
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void testConstructorWithNullMessage() {
        // Arrange: mensaje nulo
        
        // Act: se crea la excepción con mensaje nulo
        UserAlreadyExistsException exception = new UserAlreadyExistsException(null);
        
        // Assert: se verifica que acepta mensaje nulo
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testConstructorWithNullMessageAndCause() {
        // Arrange: mensaje y causa nulos
        
        // Act: se crea la excepción con parámetros nulos
        UserAlreadyExistsException exception = new UserAlreadyExistsException(null, null);
        
        // Assert: se verifica que acepta parámetros nulos
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }
}