package com.techcorp.authapp.service;

import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthenticationService
 * Objetivo: mejorar cobertura de 57% a 85%+ cubriendo todos los escenarios de negocio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService - Tests de Cobertura")
class AuthenticationServiceTest {

    // Constants para evitar duplicación de strings - SonarQube compliance
    private static final String TEST_USERNAME = "test.user";
    private static final String TEST_EMAIL = "test@techcorp.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
    private static final String USER_ID = "USR-12345";
    
    // Error messages
    private static final String USER_ALREADY_EXISTS_MESSAGE = "Nombre de usuario ya registrado";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    private static final String ACCOUNT_INACTIVE_MESSAGE = "Account is inactive";
    private static final String INVALID_TOKEN_MESSAGE = "Token inválido";
    private static final String INVALID_OR_EXPIRED_TOKEN_MESSAGE = "Token inválido o expirado";

    @Mock
    private InMemoryUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerationService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRegistrationDto registrationDto;
    private LoginRequestDto loginRequest;
    private SystemUser testUser;

    @BeforeEach
    void setUp() {
        // Arrange: se inicializan los DTOs de prueba
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(TEST_USERNAME);
        registrationDto.setEmailAddress(TEST_EMAIL);
        registrationDto.setPassword(TEST_PASSWORD);

        loginRequest = new LoginRequestDto();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        testUser = new SystemUser();
        testUser.setUserId(USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmailAddress(TEST_EMAIL);
        testUser.setEncodedPassword(ENCODED_PASSWORD);
        testUser.setAccountActive(true);
    }

    @Test
    @DisplayName("registerNewUser - Usuario nuevo registrado exitosamente")
    void testRegisterNewUserSuccessfully() {
        // Arrange: se configura el mock para usuario no existente
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.saveUser(any(SystemUser.class))).thenReturn(testUser);

        // Act: se registra el usuario
        SystemUser result = authenticationService.registerNewUser(registrationDto);

        // Assert: se verifica el resultado
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(result.getEmailAddress()).isEqualTo(TEST_EMAIL);

        verify(userRepository, times(1)).existsByUsername(TEST_USERNAME);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepository, times(1)).saveUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("registerNewUser - Usuario ya existe lanza UserAlreadyExistsException")
    void testRegisterNewUserWhenUserAlreadyExists() {
        // Arrange: se configura el mock para usuario existente
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.registerNewUser(registrationDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage(USER_ALREADY_EXISTS_MESSAGE);

        verify(userRepository, times(1)).existsByUsername(TEST_USERNAME);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).saveUser(any(SystemUser.class));
    }

    @Test
    @DisplayName("authenticateUser - Autenticación exitosa retorna token")
    void testAuthenticateUserSuccessfully() {
        // Arrange: se configura el mock para autenticación exitosa
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(tokenService.generateUserToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

        // Act: se autentica el usuario
        String result = authenticationService.authenticateUser(loginRequest);

        // Assert: se verifica el token
        assertThat(result).isEqualTo(TEST_TOKEN);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, times(1)).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, times(1)).generateUserToken(TEST_USERNAME);
        verify(userRepository, times(1)).storeUserToken(TEST_USERNAME, TEST_TOKEN);
    }

    @Test
    @DisplayName("authenticateUser - Usuario no encontrado lanza UserNotFoundException")
    void testAuthenticateUserWhenUserNotFound() {
        // Arrange: se configura el mock para usuario no encontrado
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.authenticateUser(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage(USER_NOT_FOUND_MESSAGE);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateUserToken(anyString());
    }

    @Test
    @DisplayName("authenticateUser - Contraseña incorrecta lanza InvalidCredentialsException")
    void testAuthenticateUserWithIncorrectPassword() {
        // Arrange: se configura el mock para contraseña incorrecta
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.authenticateUser(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage(INVALID_CREDENTIALS_MESSAGE);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, times(1)).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, never()).generateUserToken(anyString());
    }

    @Test
    @DisplayName("authenticateUser - Cuenta inactiva lanza RuntimeException")
    void testAuthenticateUserWithInactiveAccount() {
        // Arrange: se configura usuario inactivo
        testUser.setAccountActive(false);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.authenticateUser(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ACCOUNT_INACTIVE_MESSAGE);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, times(1)).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, never()).generateUserToken(anyString());
    }

    @Test
    @DisplayName("logoutUser - Logout exitoso remueve token del repositorio")
    void testLogoutUserSuccessfully() {
        // Arrange: no hay configuración especial necesaria

        // Act: se hace logout del usuario
        authenticationService.logoutUser(TEST_USERNAME);

        // Assert: se verifica que se removió el token
        verify(userRepository, times(1)).removeUserToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("validateTokenAndGetUsername - Token válido retorna username")
    void testValidateTokenAndGetUsernameSuccessfully() {
        // Arrange: se configura token válido
        when(tokenService.validateTokenAndGetUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userRepository.isTokenValid(TEST_USERNAME, TEST_TOKEN)).thenReturn(true);

        // Act: se valida el token
        String result = authenticationService.validateTokenAndGetUsername(TEST_TOKEN);

        // Assert: se verifica el username
        assertThat(result).isEqualTo(TEST_USERNAME);

        verify(tokenService, times(1)).validateTokenAndGetUsername(TEST_TOKEN);
        verify(userRepository, times(1)).isTokenValid(TEST_USERNAME, TEST_TOKEN);
    }

    @Test
    @DisplayName("validateTokenAndGetUsername - Token nulo lanza RuntimeException")
    void testValidateTokenAndGetUsernameWithNullToken() {
        // Arrange: token nulo

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.validateTokenAndGetUsername(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(INVALID_TOKEN_MESSAGE);

        verify(tokenService, never()).validateTokenAndGetUsername(anyString());
        verify(userRepository, never()).isTokenValid(anyString(), anyString());
    }

    @Test
    @DisplayName("validateTokenAndGetUsername - Token vacío lanza RuntimeException")
    void testValidateTokenAndGetUsernameWithEmptyToken() {
        // Arrange: token vacío

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.validateTokenAndGetUsername("   "))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(INVALID_TOKEN_MESSAGE);

        verify(tokenService, never()).validateTokenAndGetUsername(anyString());
        verify(userRepository, never()).isTokenValid(anyString(), anyString());
    }

    @Test
    @DisplayName("validateTokenAndGetUsername - Token no válido en repositorio lanza RuntimeException")
    void testValidateTokenAndGetUsernameWithInvalidTokenInRepository() {
        // Arrange: token válido en servicio pero no en repositorio
        when(tokenService.validateTokenAndGetUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userRepository.isTokenValid(TEST_USERNAME, TEST_TOKEN)).thenReturn(false);

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.validateTokenAndGetUsername(TEST_TOKEN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(INVALID_TOKEN_MESSAGE);

        verify(tokenService, times(1)).validateTokenAndGetUsername(TEST_TOKEN);
        verify(userRepository, times(1)).isTokenValid(TEST_USERNAME, TEST_TOKEN);
    }

    @Test
    @DisplayName("validateTokenAndGetUsername - Excepción en validación de token lanza RuntimeException")
    void testValidateTokenAndGetUsernameWithTokenServiceException() {
        // Arrange: servicio de tokens lanza excepción
        when(tokenService.validateTokenAndGetUsername(TEST_TOKEN))
                .thenThrow(new RuntimeException("Token malformado"));

        // Act & Assert: se verifica que se lanza la excepción correcta
        assertThatThrownBy(() -> authenticationService.validateTokenAndGetUsername(TEST_TOKEN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(INVALID_TOKEN_MESSAGE);

        verify(tokenService, times(1)).validateTokenAndGetUsername(TEST_TOKEN);
        verify(userRepository, never()).isTokenValid(anyString(), anyString());
    }
}