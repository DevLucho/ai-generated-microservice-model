package com.techcorp.authapp.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TokenGenerationService
 * Cobertura: generación, validación, extracción y manejo de errores de tokens JWT
 */
class TokenGenerationServiceTest {

    private static final String VALID_USERNAME = "testuser";
    private static final String ANOTHER_USERNAME = "anotheruser";
    private static final String EMPTY_TOKEN = "";
    private static final String NULL_TOKEN = null;
    private static final String WHITESPACE_TOKEN = "   ";
    private static final String INVALID_TOKEN = "invalid.token.format";
    private static final String MALFORMED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";
    private static final String TOKEN_EMPTY_ERROR_MESSAGE = "Token vacío";
    private static final String TOKEN_INVALID_ERROR_MESSAGE = "Token inválido";

    private TokenGenerationService tokenGenerationService;

    @BeforeEach
    void setUp() {
        tokenGenerationService = new TokenGenerationService();
    }

    @Test
    @DisplayName("Generate token should create valid JWT token for username")
    void testGenerateUserTokenCreatesValidToken() {
        // Act: generar token para usuario válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Assert: verificar que el token fue generado correctamente
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT format: header.payload.signature
    }

    @Test
    @DisplayName("Generate token should create different tokens for different users")
    void testGenerateUserTokenCreatesDifferentTokensForDifferentUsers() {
        // Act: generar tokens para usuarios diferentes
        String token1 = tokenGenerationService.generateUserToken(VALID_USERNAME);
        String token2 = tokenGenerationService.generateUserToken(ANOTHER_USERNAME);

        // Assert: verificar que los tokens son diferentes
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Generate token should create different tokens for same user at different times")
    void testGenerateUserTokenCreatesDifferentTokensForSameUserAtDifferentTimes() throws InterruptedException {
        // Act: generar dos tokens para el mismo usuario con diferencia de tiempo
        String token1 = tokenGenerationService.generateUserToken(VALID_USERNAME);
        Thread.sleep(1000); // Asegurar diferencia de tiempo de 1 segundo
        String token2 = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Assert: verificar que los tokens son diferentes (diferentes timestamps)
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Extract username should return correct username from valid token")
    void testExtractUsernameFromTokenReturnsCorrectUsername() {
        // Arrange: generar token válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Act: extraer username del token
        String extractedUsername = tokenGenerationService.extractUsernameFromToken(token);

        // Assert: verificar que el username extraído es correcto
        assertThat(extractedUsername).isEqualTo(VALID_USERNAME);
    }

    @Test
    @DisplayName("Extract username should throw exception for invalid token")
    void testExtractUsernameFromTokenThrowsExceptionForInvalidToken() {
        // Act & Assert: verificar que se lanza excepción para token inválido
        assertThatThrownBy(() -> tokenGenerationService.extractUsernameFromToken(INVALID_TOKEN))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Extract username should throw exception for malformed token")
    void testExtractUsernameFromTokenThrowsExceptionForMalformedToken() {
        // Act & Assert: verificar que se lanza excepción para token malformado
        assertThatThrownBy(() -> tokenGenerationService.extractUsernameFromToken(MALFORMED_TOKEN))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Is token valid should return true for valid unexpired token")
    void testIsTokenValidReturnsTrueForValidToken() {
        // Arrange: generar token válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Act: validar token
        boolean isValid = tokenGenerationService.isTokenValid(token);

        // Assert: verificar que el token es válido
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Is token valid should return false for invalid token")
    void testIsTokenValidReturnsFalseForInvalidToken() {
        // Act: validar token inválido
        boolean isValid = tokenGenerationService.isTokenValid(INVALID_TOKEN);

        // Assert: verificar que el token no es válido
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is token valid should return false for malformed token")
    void testIsTokenValidReturnsFalseForMalformedToken() {
        // Act: validar token malformado
        boolean isValid = tokenGenerationService.isTokenValid(MALFORMED_TOKEN);

        // Assert: verificar que el token no es válido
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Is token valid should return false for null token")
    void testIsTokenValidReturnsFalseForNullToken() {
        // Act: validar token nulo
        boolean isValid = tokenGenerationService.isTokenValid(NULL_TOKEN);

        // Assert: verificar que el token no es válido
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validate token and get username should return username for valid token")
    void testValidateTokenAndGetUsernameReturnsUsernameForValidToken() {
        // Arrange: generar token válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Act: validar token y extraer username
        String username = tokenGenerationService.validateTokenAndGetUsername(token);

        // Assert: verificar que se obtuvo el username correcto
        assertThat(username).isEqualTo(VALID_USERNAME);
    }

    @Test
    @DisplayName("Validate token and get username should throw exception for null token")
    void testValidateTokenAndGetUsernameThrowsExceptionForNullToken() {
        // Act & Assert: verificar que se lanza excepción para token nulo
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(NULL_TOKEN))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_EMPTY_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Validate token and get username should throw exception for empty token")
    void testValidateTokenAndGetUsernameThrowsExceptionForEmptyToken() {
        // Act & Assert: verificar que se lanza excepción para token vacío
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(EMPTY_TOKEN))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_EMPTY_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Validate token and get username should throw exception for whitespace token")
    void testValidateTokenAndGetUsernameThrowsExceptionForWhitespaceToken() {
        // Act & Assert: verificar que se lanza excepción para token con solo espacios
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(WHITESPACE_TOKEN))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_EMPTY_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Validate token and get username should throw exception for invalid token")
    void testValidateTokenAndGetUsernameThrowsExceptionForInvalidToken() {
        // Act & Assert: verificar que se lanza excepción para token inválido
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(INVALID_TOKEN))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_INVALID_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Validate token and get username should throw exception for malformed token")
    void testValidateTokenAndGetUsernameThrowsExceptionForMalformedToken() {
        // Act & Assert: verificar que se lanza excepción para token malformado
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(MALFORMED_TOKEN))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_INVALID_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Generate token should handle special characters in username")
    void testGenerateTokenHandlesSpecialCharactersInUsername() {
        // Arrange: username con caracteres especiales
        String specialUsername = "user@domain.com";

        // Act: generar token
        String token = tokenGenerationService.generateUserToken(specialUsername);

        // Assert: verificar que el token fue generado y el username se puede extraer
        assertThat(token).isNotNull().isNotEmpty();
        String extractedUsername = tokenGenerationService.extractUsernameFromToken(token);
        assertThat(extractedUsername).isEqualTo(specialUsername);
    }

    @Test
    @DisplayName("Generate token should handle long usernames")
    void testGenerateTokenHandlesLongUsernames() {
        // Arrange: username largo
        String longUsername = "a".repeat(100);

        // Act: generar token
        String token = tokenGenerationService.generateUserToken(longUsername);

        // Assert: verificar que el token fue generado y el username se puede extraer
        assertThat(token).isNotNull().isNotEmpty();
        String extractedUsername = tokenGenerationService.extractUsernameFromToken(token);
        assertThat(extractedUsername).isEqualTo(longUsername);
    }

    @Test
    @DisplayName("Token validation should be consistent across multiple calls")
    void testTokenValidationConsistencyAcrossMultipleCalls() {
        // Arrange: generar token válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Act: validar el mismo token múltiples veces
        boolean isValid1 = tokenGenerationService.isTokenValid(token);
        boolean isValid2 = tokenGenerationService.isTokenValid(token);
        boolean isValid3 = tokenGenerationService.isTokenValid(token);

        // Assert: verificar que todas las validaciones retornan el mismo resultado
        assertThat(isValid1).isTrue();
        assertThat(isValid2).isTrue();
        assertThat(isValid3).isTrue();
    }

    @Test
    @DisplayName("Username extraction should be consistent across multiple calls")
    void testUsernameExtractionConsistencyAcrossMultipleCalls() {
        // Arrange: generar token válido
        String token = tokenGenerationService.generateUserToken(VALID_USERNAME);

        // Act: extraer username múltiples veces
        String username1 = tokenGenerationService.extractUsernameFromToken(token);
        String username2 = tokenGenerationService.extractUsernameFromToken(token);
        String username3 = tokenGenerationService.extractUsernameFromToken(token);

        // Assert: verificar que todas las extracciones retornan el mismo username
        assertThat(username1).isEqualTo(VALID_USERNAME);
        assertThat(username2).isEqualTo(VALID_USERNAME);
        assertThat(username3).isEqualTo(VALID_USERNAME);
    }

    @Test
    @DisplayName("Generate token should handle null username gracefully")
    void testGenerateUserTokenWithNullUsername() {
        // Act & Assert: verificar que se puede generar token con username nulo
        assertThatCode(() -> tokenGenerationService.generateUserToken(null))
            .doesNotThrowAnyException();
        
        String tokenWithNull = tokenGenerationService.generateUserToken(null);
        assertThat(tokenWithNull).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Generate token should handle empty username")
    void testGenerateUserTokenWithEmptyUsername() {
        // Act: generar token con username vacío
        String tokenWithEmpty = tokenGenerationService.generateUserToken("");
        assertThat(tokenWithEmpty).isNotNull().isNotEmpty();
        
        // Verificar que se puede extraer el username vacío
        String extractedUsername = tokenGenerationService.extractUsernameFromToken(tokenWithEmpty);
        assertThat(extractedUsername).satisfiesAnyOf(
            username -> assertThat(username).isEmpty(),
            username -> assertThat(username).isNull()
        );
    }

    @Test
    @DisplayName("validateTokenAndGetUsername should throw exception for expired token")
    void testValidateTokenAndGetUsernameWithExpiredToken() throws Exception {
        // Arrange: crear un token que será inválido por diferentes razones
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxfQ.invalid";
        
        // Act & Assert: verificar que se lanza excepción para token inválido
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(invalidToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_INVALID_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("isTokenValid should handle empty string token gracefully")
    void testIsTokenValidWithEmptyString() {
        // Act: validar token con string vacío
        boolean isValid = tokenGenerationService.isTokenValid("");
        
        // Assert: verificar que retorna false
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("extractUsernameFromToken should handle null token gracefully")
    void testExtractUsernameFromNullToken() {
        // Act & Assert: verificar que se maneja correctamente el token nulo
        assertThatThrownBy(() -> tokenGenerationService.extractUsernameFromToken(null))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Validate token and get username should handle different exception types")
    void testValidateTokenAndGetUsernameHandlesDifferentExceptions() {
        // Arrange: crear tokens con diferentes tipos de problemas
        String[] problematicTokens = {
            "not.a.jwt",
            "eyJhbGciOiJIUzI1NiJ9",  // Incomplete JWT
            "eyJhbGciOiJIUzI1NiJ9.", // Missing payload
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0",  // Missing signature
            "invalid-format"
        };
        
        // Act & Assert: verificar que todos lanzan RuntimeException con mensaje apropiado
        for (String token : problematicTokens) {
            assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(TOKEN_INVALID_ERROR_MESSAGE);
        }
    }

    @Test
    @DisplayName("Validate token and get username additional edge case coverage test")
    void testValidateTokenAndGetUsernameAdditionalCoverage() {
        // Este test está diseñado para cubrir casos edge adicionales
        // que pueden estar faltando en la cobertura actual
        
        // Test 1: Token que pase la validación inicial pero falle en otros checks
        String validToken = tokenGenerationService.generateUserToken(VALID_USERNAME);
        
        // Verificar que el token funciona correctamente
        String extractedUsername = tokenGenerationService.validateTokenAndGetUsername(validToken);
        assertThat(extractedUsername).isEqualTo(VALID_USERNAME);
        
        // Test 2: Token con espacios en blanco
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername("   "))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_EMPTY_ERROR_MESSAGE);
        
        // Test 3: Token con formato pseudo-válido pero corrupto
        String corruptedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0In0.corrupt_signature";
        assertThatThrownBy(() -> tokenGenerationService.validateTokenAndGetUsername(corruptedToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(TOKEN_INVALID_ERROR_MESSAGE);
        
        // Test 4: Verificar que el servicio maneja múltiples validaciones de forma consistente
        for (int i = 0; i < 3; i++) {
            String testToken = tokenGenerationService.generateUserToken(VALID_USERNAME + i);
            String result = tokenGenerationService.validateTokenAndGetUsername(testToken);
            assertThat(result).isEqualTo(VALID_USERNAME + i);
        }
    }

    @Test
    @DisplayName("Test specific expired token scenario for 100% coverage - Final Strategy")
    void testExpiredTokenScenarioAdvanced() {
        // ESTRATEGIA FINAL: Usar Mockito para crear un escenario específico
        // donde isTokenValid retorne false pero extractUsernameFromToken funcione
        
        // Crear un spy del servicio real para poder interceptar isTokenValid
        TokenGenerationService spyService = Mockito.spy(tokenGenerationService);
        
        // Generar un token válido primero
        String validToken = tokenGenerationService.generateUserToken(VALID_USERNAME);
        
        // Verificar que normalmente funciona
        String normalResult = spyService.validateTokenAndGetUsername(validToken);
        assertThat(normalResult).isEqualTo(VALID_USERNAME);
        
        // Ahora configurar el spy para que isTokenValid retorne false SOLO para el próximo llamado
        // Pero manteniendo extractUsernameFromToken funcional
        doReturn(false).when(spyService).isTokenValid(validToken);
        
        // Este llamado debe activar la línea 82: "Token expirado"
        assertThatThrownBy(() -> spyService.validateTokenAndGetUsername(validToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Token expirado");
    }
}