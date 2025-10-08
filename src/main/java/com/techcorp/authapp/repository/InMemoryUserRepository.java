package com.techcorp.authapp.repository;

import com.techcorp.authapp.model.SystemUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio en memoria para el almacenamiento de datos de usuarios TechCorp
 * Proporciona operaciones CRUD básicas para la gestión de usuarios
 */
@Repository
public class InMemoryUserRepository {
    
    private final Map<String, SystemUser> userStore = new ConcurrentHashMap<>();
    private final Map<String, String> userTokens = new ConcurrentHashMap<>();
    
    /**
     * Almacena un nuevo usuario en memoria
     */
    public SystemUser saveUser(SystemUser user) {
        userStore.put(user.getUsername(), user);
        return user;
    }
    
    /**
     * Busca un usuario por nombre de usuario
     */
    public Optional<SystemUser> findByUsername(String username) {
        return Optional.ofNullable(userStore.get(username));
    }
    
    /**
     * Busca un usuario por ID
     */
    public Optional<SystemUser> findByUserId(String userId) {
        return userStore.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }
    
    /**
     * Obtiene todos los usuarios registrados
     */
    public List<SystemUser> findAllUsers() {
        return new ArrayList<>(userStore.values());
    }
    
    /**
     * Actualiza un usuario existente
     */
    public SystemUser updateUser(SystemUser user) {
        if (userStore.containsKey(user.getUsername())) {
            userStore.put(user.getUsername(), user);
            return user;
        }
        throw new RuntimeException("User not found for update: " + user.getUsername());
    }
    
    /**
     * Elimina un usuario del repositorio
     */
    public boolean deleteUser(String username) {
        SystemUser removedUser = userStore.remove(username);
        if (removedUser != null) {
            // También remover el token si existe
            userTokens.remove(username);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si un nombre de usuario ya existe
     */
    public boolean existsByUsername(String username) {
        return userStore.containsKey(username);
    }
    
    /**
     * Verifica si un email ya existe
     */
    public boolean existsByEmail(String email) {
        return userStore.values().stream()
                .anyMatch(user -> user.getEmailAddress().equals(email));
    }
    
    /**
     * Cuenta el total de usuarios registrados
     */
    public long countUsers() {
        return userStore.size();
    }
    
    /**
     * Cuenta usuarios activos
     */
    public long countActiveUsers() {
        return userStore.values().stream()
                .filter(SystemUser::isAccountActive)
                .count();
    }
    
    /**
     * Almacena el token de sesión del usuario
     */
    public void storeUserToken(String username, String token) {
        userTokens.put(username, token);
    }
    
    /**
     * Remueve el token de sesión del usuario
     */
    public void removeUserToken(String username) {
        userTokens.remove(username);
    }
    
    /**
     * Valida si el usuario tiene un token activo
     */
    public boolean hasActiveToken(String username) {
        return userTokens.containsKey(username);
    }
    
    /**
     * Obtiene el token activo de un usuario
     */
    public Optional<String> getUserToken(String username) {
        return Optional.ofNullable(userTokens.get(username));
    }
    
    /**
     * Limpia todos los tokens de sesión
     */
    public void clearAllTokens() {
        userTokens.clear();
    }
    
    /**
     * Valida si un token específico es válido para un usuario - TC008
     */
    public boolean isTokenValid(String username, String token) {
        String storedToken = userTokens.get(username);
        return storedToken != null && storedToken.equals(token);
    }
}
