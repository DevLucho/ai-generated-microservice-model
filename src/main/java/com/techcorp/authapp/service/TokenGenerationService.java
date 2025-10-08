package com.techcorp.authapp.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service for JWT token generation and validation
 */
@Service
public class TokenGenerationService {
    
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tokenValidityTime = 3600000; // 1 hour
    
    /**
     * Generate JWT token for user
     */
    public String generateUserToken(String username) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + tokenValidityTime);
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(currentDate)
            .setExpiration(expirationDate)
            .signWith(secretKey)
            .compact();
    }
    
    /**
     * Extract username from token
     */
    public String extractUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    /**
     * Validate token expiration
     */
    public boolean isTokenValid(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate token and extract username - TC008
     */
    public String validateTokenAndGetUsername(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Token vacío");
            }
            
            String username = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
                
            // Verificar que el token no haya expirado
            if (!isTokenValid(token)) {
                throw new RuntimeException("Token expirado");
            }
            
            return username;
            
        } catch (Exception e) {
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }
    }
}
