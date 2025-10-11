package com.techcorp.authapp.repository;

import com.techcorp.authapp.model.SystemUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para InMemoryUserRepository para mejorar cobertura de código
 * Repositorio con cobertura inicial del 25% - necesita tests completos
 */
@DisplayName("InMemoryUserRepository - Tests de Cobertura")
class InMemoryUserRepositoryTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String TEST_USERNAME_1 = "test.user1";
    private static final String TEST_USERNAME_2 = "test.user2";
    private static final String TEST_EMAIL_1 = "test.user1@techcorp.com";
    private static final String TEST_EMAIL_2 = "test.user2@techcorp.com";
    private static final String TEST_USER_ID_1 = "USR-12345";
    private static final String TEST_USER_ID_2 = "USR-67890";
    private static final String NONEXISTENT_USERNAME = "nonexistent.user";
    private static final String NONEXISTENT_USER_ID = "USR-99999";
    private static final String TEST_TOKEN = "test-token-12345";
    private static final String UPDATED_EMAIL = "updated.email@techcorp.com";

    private InMemoryUserRepository repository;
    private SystemUser testUser1;
    private SystemUser testUser2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        
        // Configurar usuarios de prueba
        testUser1 = new SystemUser();
        testUser1.setUserId(TEST_USER_ID_1);
        testUser1.setUsername(TEST_USERNAME_1);
        testUser1.setEmailAddress(TEST_EMAIL_1);
        testUser1.setEncodedPassword("encodedPassword123");
        testUser1.setRegistrationDate(LocalDateTime.now());
        testUser1.setAccountActive(true);

        testUser2 = new SystemUser();
        testUser2.setUserId(TEST_USER_ID_2);
        testUser2.setUsername(TEST_USERNAME_2);
        testUser2.setEmailAddress(TEST_EMAIL_2);
        testUser2.setEncodedPassword("encodedPassword456");
        testUser2.setRegistrationDate(LocalDateTime.now());
        testUser2.setAccountActive(false); // Usuario inactivo para testing
    }

    @Test
    @DisplayName("saveUser - Debe guardar un usuario correctamente")
    void testSaveUserStoresUserCorrectly() {
        // Act
        SystemUser savedUser = repository.saveUser(testUser1);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(TEST_USERNAME_1);
        assertThat(savedUser.getEmailAddress()).isEqualTo(TEST_EMAIL_1);
    }

    @Test
    @DisplayName("findByUsername - Debe encontrar usuario existente")
    void testFindByUsernameReturnsExistingUser() {
        // Arrange
        repository.saveUser(testUser1);

        // Act
        Optional<SystemUser> foundUser = repository.findByUsername(TEST_USERNAME_1);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(TEST_USERNAME_1);
        assertThat(foundUser.get().getEmailAddress()).isEqualTo(TEST_EMAIL_1);
    }

    @Test
    @DisplayName("findByUsername - Debe retornar Optional.empty para usuario inexistente")
    void testFindByUsernameReturnsEmptyForNonexistentUser() {
        // Act
        Optional<SystemUser> foundUser = repository.findByUsername(NONEXISTENT_USERNAME);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("findByUserId - Debe encontrar usuario por ID")
    void testFindByUserIdReturnsExistingUser() {
        // Arrange
        repository.saveUser(testUser1);

        // Act
        Optional<SystemUser> foundUser = repository.findByUserId(TEST_USER_ID_1);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(TEST_USER_ID_1);
        assertThat(foundUser.get().getUsername()).isEqualTo(TEST_USERNAME_1);
    }

    @Test
    @DisplayName("findByUserId - Debe retornar Optional.empty para ID inexistente")
    void testFindByUserIdReturnsEmptyForNonexistentId() {
        // Act
        Optional<SystemUser> foundUser = repository.findByUserId(NONEXISTENT_USER_ID);

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("findAllUsers - Debe retornar lista vacía cuando no hay usuarios")
    void testFindAllUsersReturnsEmptyListWhenNoUsers() {
        // Act
        List<SystemUser> users = repository.findAllUsers();

        // Assert
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findAllUsers - Debe retornar todos los usuarios guardados")
    void testFindAllUsersReturnsAllStoredUsers() {
        // Arrange
        repository.saveUser(testUser1);
        repository.saveUser(testUser2);

        // Act
        List<SystemUser> users = repository.findAllUsers();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users).extracting(SystemUser::getUsername)
                .containsExactlyInAnyOrder(TEST_USERNAME_1, TEST_USERNAME_2);
    }

    @Test
    @DisplayName("updateUser - Debe actualizar usuario existente")
    void testUpdateUserUpdatesExistingUser() {
        // Arrange
        repository.saveUser(testUser1);
        testUser1.setEmailAddress(UPDATED_EMAIL);

        // Act
        SystemUser updatedUser = repository.updateUser(testUser1);

        // Assert
        assertThat(updatedUser.getEmailAddress()).isEqualTo(UPDATED_EMAIL);
        
        Optional<SystemUser> retrievedUser = repository.findByUsername(TEST_USERNAME_1);
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmailAddress()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @DisplayName("updateUser - Debe lanzar excepción para usuario inexistente")
    void testUpdateUserThrowsExceptionForNonexistentUser() {
        // Arrange
        SystemUser nonexistentUser = new SystemUser();
        nonexistentUser.setUsername(NONEXISTENT_USERNAME);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            repository.updateUser(nonexistentUser)
        );
        assertThat(exception.getMessage()).contains("User not found for update");
    }

    @Test
    @DisplayName("deleteUser - Debe eliminar usuario existente")
    void testDeleteUserRemovesExistingUser() {
        // Arrange
        repository.saveUser(testUser1);

        // Act
        boolean deleted = repository.deleteUser(TEST_USERNAME_1);

        // Assert
        assertThat(deleted).isTrue();
        assertThat(repository.findByUsername(TEST_USERNAME_1)).isEmpty();
    }

    @Test
    @DisplayName("deleteUser - Debe retornar false para usuario inexistente")
    void testDeleteUserReturnsFalseForNonexistentUser() {
        // Act
        boolean deleted = repository.deleteUser(NONEXISTENT_USERNAME);

        // Assert
        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("countUsers - Debe contar correctamente el número de usuarios")
    void testCountUsersReturnsCorrectCount() {
        // Arrange - Sin usuarios
        assertThat(repository.countUsers()).isZero();

        // Agregar un usuario
        repository.saveUser(testUser1);
        assertThat(repository.countUsers()).isOne();

        // Agregar segundo usuario
        repository.saveUser(testUser2);
        assertThat(repository.countUsers()).isEqualTo(2L);
    }

    @Test
    @DisplayName("countActiveUsers - Debe contar solo usuarios activos")
    void testCountActiveUsersCountsOnlyActiveUsers() {
        // Arrange
        repository.saveUser(testUser1); // activo
        repository.saveUser(testUser2); // inactivo

        // Act
        long activeCount = repository.countActiveUsers();

        // Assert
        assertThat(activeCount).isOne(); // Solo testUser1 está activo
    }

    @Test
    @DisplayName("storeUserToken - Debe almacenar token de usuario")
    void testStoreUserTokenStoresTokenCorrectly() {
        // Arrange
        repository.saveUser(testUser1);

        // Act
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);

        // Assert
        Optional<String> storedToken = repository.getUserToken(TEST_USERNAME_1);
        assertThat(storedToken).isPresent();
        assertThat(storedToken.get()).isEqualTo(TEST_TOKEN);
    }

    @Test
    @DisplayName("getUserToken - Debe retornar Optional.empty para usuario sin token")
    void testGetUserTokenReturnsEmptyForUserWithoutToken() {
        // Arrange
        repository.saveUser(testUser1);

        // Act
        Optional<String> token = repository.getUserToken(TEST_USERNAME_1);

        // Assert
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("removeUserToken - Debe eliminar token de usuario")
    void testRemoveUserTokenRemovesTokenCorrectly() {
        // Arrange
        repository.saveUser(testUser1);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);

        // Act
        repository.removeUserToken(TEST_USERNAME_1);

        // Assert
        Optional<String> token = repository.getUserToken(TEST_USERNAME_1);
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("deleteUser con token - Debe eliminar usuario y su token")
    void testDeleteUserWithTokenRemovesBothUserAndToken() {
        // Arrange
        repository.saveUser(testUser1);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);

        // Act
        boolean deleted = repository.deleteUser(TEST_USERNAME_1);

        // Assert
        assertThat(deleted).isTrue();
        assertThat(repository.findByUsername(TEST_USERNAME_1)).isEmpty();
        assertThat(repository.getUserToken(TEST_USERNAME_1)).isEmpty();
    }

    @Test
    @DisplayName("hasActiveToken - Debe verificar existencia de token activo")
    void testHasActiveTokenVerifiesTokenExistence() {
        // Arrange
        repository.saveUser(testUser1);
        
        // Sin token
        assertThat(repository.hasActiveToken(TEST_USERNAME_1)).isFalse();

        // Con token
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);
        assertThat(repository.hasActiveToken(TEST_USERNAME_1)).isTrue();
    }

    @Test
    @DisplayName("Operaciones concurrentes - Debe manejar múltiples operaciones")
    void testConcurrentOperationsHandledCorrectly() {
        // Arrange & Act - Múltiples operaciones
        repository.saveUser(testUser1);
        repository.saveUser(testUser2);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);
        
        testUser1.setEmailAddress(UPDATED_EMAIL);
        repository.updateUser(testUser1);

        // Assert - Verificar estado final
        assertThat(repository.countUsers()).isEqualTo(2L);
        assertThat(repository.countActiveUsers()).isOne();
        assertThat(repository.getUserToken(TEST_USERNAME_1)).isPresent();
        
        Optional<SystemUser> updatedUser = repository.findByUsername(TEST_USERNAME_1);
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getEmailAddress()).isEqualTo(UPDATED_EMAIL);
    }

    // ========================================
    // TESTS ADICIONALES PARA 100% COBERTURA
    // ========================================

    @Test
    @DisplayName("existsByEmail - Debe detectar email existente")
    void testExistsByEmailDetectsExistingEmail() {
        // Arrange
        repository.saveUser(testUser1);

        // Act & Assert
        assertThat(repository.existsByEmail(TEST_EMAIL_1)).isTrue();
        assertThat(repository.existsByEmail("nonexistent@email.com")).isFalse();
    }

    @Test
    @DisplayName("existsByEmail - Debe retornar false para repositorio vacío")
    void testExistsByEmailReturnsFalseForEmptyRepository() {
        // Act & Assert
        assertThat(repository.existsByEmail(TEST_EMAIL_1)).isFalse();
    }

    @Test
    @DisplayName("clearAllTokens - Debe limpiar todos los tokens")
    void testClearAllTokensRemovesAllTokens() {
        // Arrange
        repository.saveUser(testUser1);
        repository.saveUser(testUser2);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);
        repository.storeUserToken(TEST_USERNAME_2, "another-token");

        // Verificar que los tokens existen
        assertThat(repository.hasActiveToken(TEST_USERNAME_1)).isTrue();
        assertThat(repository.hasActiveToken(TEST_USERNAME_2)).isTrue();

        // Act
        repository.clearAllTokens();

        // Assert
        assertThat(repository.hasActiveToken(TEST_USERNAME_1)).isFalse();
        assertThat(repository.hasActiveToken(TEST_USERNAME_2)).isFalse();
        assertThat(repository.getUserToken(TEST_USERNAME_1)).isEmpty();
        assertThat(repository.getUserToken(TEST_USERNAME_2)).isEmpty();
    }

    @Test
    @DisplayName("isTokenValid - Debe validar token correcto")
    void testIsTokenValidReturnsTrueForValidToken() {
        // Arrange
        repository.saveUser(testUser1);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);

        // Act & Assert
        assertThat(repository.isTokenValid(TEST_USERNAME_1, TEST_TOKEN)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid - Debe rechazar token incorrecto")
    void testIsTokenValidReturnsFalseForInvalidToken() {
        // Arrange
        repository.saveUser(testUser1);
        repository.storeUserToken(TEST_USERNAME_1, TEST_TOKEN);

        // Act & Assert
        assertThat(repository.isTokenValid(TEST_USERNAME_1, "wrong-token")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - Debe rechazar usuario sin token")
    void testIsTokenValidReturnsFalseForUserWithoutToken() {
        // Arrange
        repository.saveUser(testUser1);
        // No almacenar token

        // Act & Assert
        assertThat(repository.isTokenValid(TEST_USERNAME_1, TEST_TOKEN)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - Debe rechazar usuario inexistente")
    void testIsTokenValidReturnsFalseForNonexistentUser() {
        // Act & Assert
        assertThat(repository.isTokenValid(NONEXISTENT_USERNAME, TEST_TOKEN)).isFalse();
    }
}