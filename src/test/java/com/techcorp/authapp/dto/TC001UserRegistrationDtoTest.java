package com.techcorp.authapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para UserRegistrationDto - Cobertura TC001
 * 
 * Valida que los datos de entrada para TC001 cumplan con las validaciones:
 * - Username válido (3-50 caracteres)
 * - Password válida (mínimo 6 caracteres)  
 * - Email válido (formato correcto)
 */
@DisplayName("TC001 - UserRegistrationDto - Tests de Validación")
class TC001UserRegistrationDtoTest {

    private Validator validator;
    private UserRegistrationDto validDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // DTO válido base para TC001
        validDto = new UserRegistrationDto();
        validDto.setUsername("usuario.valido");
        validDto.setPassword("password123");
        validDto.setEmailAddress("usuario@techcorp.com");
    }

    @Nested
    @DisplayName("TC001.DTO.1 - Validación de Datos Válidos (Casos Positivos)")
    class ValidDataValidation {

        @Test
        @DisplayName("Debe validar DTO completamente válido para TC001")
        void testTC001_DtoValidoCompleto() {
            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Debe aceptar username con longitud mínima (3 caracteres)")
        void testTC001_UsernameMinimo() {
            // Arrange
            validDto.setUsername("abc");

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Debe aceptar username con longitud máxima (50 caracteres)")
        void testTC001_UsernameMaximo() {
            // Arrange
            validDto.setUsername("a".repeat(50));

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Debe aceptar password con longitud mínima (6 caracteres)")
        void testTC001_PasswordMinima() {
            // Arrange
            validDto.setPassword("123456");

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Debe aceptar email con formato válido estándar")
        void testTC001_EmailFormatoEstandar() {
            // Arrange
            String[] validEmails = {
                "test@techcorp.com",
                "usuario.nombre@techcorp.com", 
                "test123@subdomain.techcorp.com",
                "user+tag@techcorp.com",
                "simple@domain.co"
            };

            for (String email : validEmails) {
                validDto.setEmailAddress(email);

                // Act
                Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

                // Assert
                assertThat(violations)
                    .as("Email válido debe pasar validación: " + email)
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("TC001.DTO.2 - Validación de Límites (Casos de Borde)")
    class BoundaryValidation {

        @Test
        @DisplayName("Debe rechazar username menor a 3 caracteres")
        void testTC001_UsernameMenorMinimo() {
            // Arrange
            validDto.setUsername("ab");

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("El nombre de usuario debe tener entre 3 y 50 caracteres");
        }

        @Test
        @DisplayName("Debe rechazar username mayor a 50 caracteres")
        void testTC001_UsernameMayorMaximo() {
            // Arrange
            validDto.setUsername("a".repeat(51));

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("El nombre de usuario debe tener entre 3 y 50 caracteres");
        }

        @Test
        @DisplayName("Debe rechazar password menor a 6 caracteres")
        void testTC001_PasswordMenorMinima() {
            // Arrange
            validDto.setPassword("12345");

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("La contraseña debe tener al menos 6 caracteres");
        }
    }

    @Nested
    @DisplayName("TC001.DTO.3 - Validación de Campos Requeridos")
    class RequiredFieldsValidation {

        @Test
        @DisplayName("Debe rechazar username nulo")
        void testTC001_UsernameNulo() {
            // Arrange
            validDto.setUsername(null);

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("El nombre de usuario es requerido");
        }

        @Test
        @DisplayName("Debe rechazar username vacío")
        void testTC001_UsernameVacio() {
            // Arrange
            validDto.setUsername("");

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
            assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("El nombre de usuario es requerido"))).isTrue();
        }

        @Test
        @DisplayName("Debe rechazar password nula")
        void testTC001_PasswordNula() {
            // Arrange
            validDto.setPassword(null);

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("La contraseña es requerida");
        }

        @Test
        @DisplayName("Debe rechazar email nulo")
        void testTC001_EmailNulo() {
            // Arrange
            validDto.setEmailAddress(null);

            // Act
            Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .contains("El email es requerido");
        }
    }

    @Nested
    @DisplayName("TC001.DTO.4 - Validación de Formato Email")
    class EmailFormatValidation {

        @Test
        @DisplayName("Debe rechazar email con formato inválido")
        void testTC001_EmailFormatoInvalido() {
            // Arrange
            String[] invalidEmails = {
                "email-invalido",
                "@techcorp.com",
                "usuario@",
                "usuario.techcorp.com",
                "usuario@.com",
                "usuario@TechCorp.",
                ""
            };

            for (String invalidEmail : invalidEmails) {
                validDto.setEmailAddress(invalidEmail);

                // Act
                Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(validDto);

                // Assert
                assertThat(violations)
                    .as("Email inválido debe fallar validación: " + invalidEmail)
                    .hasSizeGreaterThanOrEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("TC001.DTO.5 - Tests de Constructores y Getters/Setters")
    class ConstructorAndAccessorTests {

        @Test
        @DisplayName("Debe crear DTO con constructor por defecto")
        void testTC001_ConstructorPorDefecto() {
            // Act
            UserRegistrationDto dto = new UserRegistrationDto();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getPassword()).isNull();
            assertThat(dto.getEmailAddress()).isNull();
        }

        @Test
        @DisplayName("Debe configurar y obtener todos los campos correctamente")
        void testTC001_GettersSetters() {
            // Arrange
            UserRegistrationDto dto = new UserRegistrationDto();
            String username = "test.user";
            String password = "testpass123";
            String email = "test@techcorp.com";

            // Act
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setEmailAddress(email);

            // Assert
            assertThat(dto.getUsername()).isEqualTo(username);
            assertThat(dto.getPassword()).isEqualTo(password);
            assertThat(dto.getEmailAddress()).isEqualTo(email);
        }
    }
}
