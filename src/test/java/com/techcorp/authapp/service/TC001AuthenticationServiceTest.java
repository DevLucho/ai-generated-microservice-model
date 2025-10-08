package com.techcorp.authapp.service;

import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Tests unitarios para AuthenticationService - Cobertura TC001
 * 
 * Prueba la lógica de negocio del registro de usuarios para el caso TC001:
 * - Validación de usuario no existente
 * - Encriptación de contraseña
 * - Generación de ID único
 * - Persistencia del usuario
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TC001 - AuthenticationService - Tests Unitarios")
class TC001AuthenticationServiceTest {

    @Mock
    private InMemoryUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerationService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserRegistrationDto validRegistrationDto;

    @BeforeEach
    void setUp() {
        validRegistrationDto = new UserRegistrationDto();
        validRegistrationDto.setUsername("usuario.valido");
        validRegistrationDto.setPassword("password123");
        validRegistrationDto.setEmailAddress("usuario@techcorp.com");
    }

    @Nested
    @DisplayName("TC001.Service.1 - Validación de Usuario No Existente")
    class UserExistenceValidation {

        @Test
        @DisplayName("Debe verificar que usuario no existe antes de registrar")
        void testRegistroExitoso_DebeVerificarUsuarioNoExiste() {
            // Arrange
            when(userRepository.existsByUsername(validRegistrationDto.getUsername()))
                    .thenReturn(false); // Usuario no existe
            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encodedPassword123");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            verify(userRepository, times(1)).existsByUsername(validRegistrationDto.getUsername());
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(validRegistrationDto.getUsername());
        }
    }

    @Nested
    @DisplayName("TC001.Service.2 - Encriptación de Contraseña")
    class PasswordEncryptionValidation {

        @Test
        @DisplayName("Debe encriptar contraseña antes de guardar usuario")
        void testRegistroExitoso_DebeEncriptarPassword() {
            // Arrange
            String encodedPassword = "hashedPassword123";
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(validRegistrationDto.getPassword()))
                    .thenReturn(encodedPassword);
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            verify(passwordEncoder, times(1)).encode(validRegistrationDto.getPassword());
            assertThat(result.getEncodedPassword()).isEqualTo(encodedPassword);
            assertThat(result.getEncodedPassword()).isNotEqualTo(validRegistrationDto.getPassword());
        }

        @Test
        @DisplayName("No debe almacenar contraseña en texto plano")
        void testRegistroExitoso_NoDebeAlmacenarPasswordTextoPlano() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            assertThat(result.getEncodedPassword()).isNotEqualTo("password123");
            assertThat(result.getEncodedPassword()).isEqualTo("hashedPassword");
        }
    }

    @Nested
    @DisplayName("TC001.Service.3 - Generación de ID Único")
    class UserIdGenerationValidation {

        @Test
        @DisplayName("Debe generar ID único para cada usuario")
        void testRegistroExitoso_DebeGenerarIdUnico() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result1 = authenticationService.registerNewUser(validRegistrationDto);
            
            UserRegistrationDto dto2 = new UserRegistrationDto();
            dto2.setUsername("otro.usuario");
            dto2.setPassword("password456");
            dto2.setEmailAddress("otro@techcorp.com");
            
            SystemUser result2 = authenticationService.registerNewUser(dto2);

            // Assert
            assertThat(result1.getUserId()).isNotNull();
            assertThat(result2.getUserId()).isNotNull();
            assertThat(result1.getUserId()).isNotEqualTo(result2.getUserId());
            assertThat(result1.getUserId()).matches("^[a-f0-9\\-]{36}$"); // UUID format
        }
    }

    @Nested
    @DisplayName("TC001.Service.4 - Persistencia de Usuario")
    class UserPersistenceValidation {

        @Test
        @DisplayName("Debe guardar usuario con todos los datos completos")
        void testRegistroExitoso_DebeGuardarUsuarioCompleto() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            verify(userRepository, times(1)).saveUser(any(SystemUser.class));
            
            assertThat(result.getUserId()).isNotNull();
            assertThat(result.getUsername()).isEqualTo(validRegistrationDto.getUsername());
            assertThat(result.getEmailAddress()).isEqualTo(validRegistrationDto.getEmailAddress());
            assertThat(result.getEncodedPassword()).isNotNull();
            assertThat(result.getRegistrationDate()).isNotNull();
            assertThat(result.isAccountActive()).isTrue(); // Cuenta activa por defecto
        }

        @Test
        @DisplayName("Debe llamar al repositorio exactamente una vez")
        void testRegistroExitoso_DebeInvocarRepositorioUnaVez() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            verify(userRepository, times(1)).existsByUsername(validRegistrationDto.getUsername());
            verify(userRepository, times(1)).saveUser(any(SystemUser.class));
        }
    }

    @Nested
    @DisplayName("TC001.Service.5 - Validación de Datos de Entrada")
    class InputDataValidation {

        @Test
        @DisplayName("Debe procesar correctamente username válido")
        void testRegistroExitoso_DebeProcesarUsernameValido() {
            // Arrange
            String validUsername = "usuario.valido.123";
            validRegistrationDto.setUsername(validUsername);
            
            when(userRepository.existsByUsername(validUsername)).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            assertThat(result.getUsername()).isEqualTo(validUsername);
            verify(userRepository).existsByUsername(validUsername);
        }

        @Test
        @DisplayName("Debe procesar correctamente email válido")
        void testRegistroExitoso_DebeProcesarEmailValido() {
            // Arrange
            String validEmail = "usuario.test@techcorp.com";
            validRegistrationDto.setEmailAddress(validEmail);
            
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.saveUser(any(SystemUser.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SystemUser result = authenticationService.registerNewUser(validRegistrationDto);

            // Assert
            assertThat(result.getEmailAddress()).isEqualTo(validEmail);
        }
    }
}
