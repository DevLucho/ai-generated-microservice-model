package com.techcorp.authapp.debug;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.authapp.dto.LoginRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de depuración específico para WRONGPASSWORD en contexto SpringBootTest
 */
@SpringBootTest
@AutoConfigureWebMvc
public class DebugWrongPasswordFullContextTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testWrongPasswordInFullContext() throws Exception {
        // Test WRONGPASSWORD en contexto completo
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("test.user");
        loginRequest.setPassword("WRONGPASSWORD");

        System.out.println("Testing WRONGPASSWORD in @SpringBootTest context...");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())  // Should be 401
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));  // Since user doesn't exist
    }
}
