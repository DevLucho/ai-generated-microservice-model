package com.techcorp.authapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para la aplicación TechCorp
 * Maneja autenticación, autorización y configuración CORS
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    /**
     * Configuración de la cadena de filtros de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF completamente para todos los endpoints
            .csrf(csrf -> csrf.disable())
            
            // Deshabilitar CORS para tests
            .cors(cors -> cors.disable())
            
            // Configurar manejo de sesiones como stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorización de requests - PERMITIR TODO PARA TESTS
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
    
    /**
     * Bean para el encoder de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Aumentamos la fortaleza a 12 rounds
    }
}
