package com.techcorp.authapp.debug;

import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de depuración para entender por qué "WRONGPASSWORD" se considera válida
 */
@SpringBootTest
public class DebugPasswordTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void debugPasswordValidation() {
        try {
            // Primero registrar un usuario
            UserRegistrationDto registrationDto = new UserRegistrationDto();
            registrationDto.setUsername("test.user");
            registrationDto.setPassword("password123");
            registrationDto.setEmailAddress("test@techcorp.com");
            
            authenticationService.registerNewUser(registrationDto);
            System.out.println("✅ Usuario test.user registrado con password123");
            
            // Probar autenticación con contraseña correcta
            LoginRequestDto correctLogin = new LoginRequestDto();
            correctLogin.setUsername("test.user");
            correctLogin.setPassword("password123");
            
            String token1 = authenticationService.authenticateUser(correctLogin);
            System.out.println("✅ Login correcto con password123: " + (token1 != null));
            
            // Probar autenticación con WRONGPASSWORD
            LoginRequestDto wrongLogin = new LoginRequestDto();
            wrongLogin.setUsername("test.user");
            wrongLogin.setPassword("WRONGPASSWORD");
            
            try {
                String token2 = authenticationService.authenticateUser(wrongLogin);
                System.out.println("❌ ¡¡¡ERROR!!! WRONGPASSWORD fue aceptada: " + (token2 != null));
            } catch (Exception e) {
                System.out.println("✅ WRONGPASSWORD correctamente rechazada: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error en debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
