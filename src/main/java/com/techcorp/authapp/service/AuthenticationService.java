package com.techcorp.authapp.service;

import com.techcorp.authapp.dto.LoginRequestDto;
import com.techcorp.authapp.dto.UserRegistrationDto;
import com.techcorp.authapp.model.SystemUser;
import com.techcorp.authapp.repository.InMemoryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling user authentication operations
 */
@Service
public class AuthenticationService {
    
    @Autowired
    private InMemoryUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TokenGenerationService tokenService;
    
    /**
     * Register a new user in the system
     */
    public SystemUser registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            // TC002: Mensaje en español para usuario duplicado
            throw new UserAlreadyExistsException("Nombre de usuario ya registrado");
        }
        
        String userId = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        
        SystemUser newUser = new SystemUser(
            userId,
            registrationDto.getUsername(),
            encodedPassword,
            registrationDto.getEmailAddress()
        );
        
        return userRepository.saveUser(newUser);
    }
    
    /**
     * Authenticate user login
     */
    public String authenticateUser(LoginRequestDto loginRequest) {
        // TC006: Verificar si el usuario existe primero
        SystemUser user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // TC005: Verificar contraseña después de confirmar que el usuario existe
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getEncodedPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        
        if (!user.isAccountActive()) {
            throw new RuntimeException("Account is inactive");
        }
        
        String token = tokenService.generateUserToken(user.getUsername());
        userRepository.storeUserToken(user.getUsername(), token);
        
        return token;
    }
    
    /**
     * Logout user from the system
     */
    public void logoutUser(String username) {
        userRepository.removeUserToken(username);
    }
    
    /**
     * Validate JWT token and extract username
     * TC008: Validación de token para logout
     */
    public String validateTokenAndGetUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token inválido");
        }
        
        try {
            // Validar token usando el servicio de tokens
            String username = tokenService.validateTokenAndGetUsername(token);
            
            // Verificar que el token esté almacenado en el repositorio
            if (!userRepository.isTokenValid(username, token)) {
                throw new RuntimeException("Token inválido o expirado");
            }
            
            return username;
            
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }
}
