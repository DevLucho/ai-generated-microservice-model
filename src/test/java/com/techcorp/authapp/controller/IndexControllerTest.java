package com.techcorp.authapp.controller;

import com.techcorp.authapp.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para IndexController
 * Verifica las redirecciones hacia Swagger UI
 */
@WebMvcTest(IndexController.class)
@Import(TestSecurityConfig.class)
class IndexControllerTest {

    private static final String SWAGGER_UI_URL = "/swagger-ui.html";
    private static final String LOCATION_HEADER = "Location";
    private static final String DOCS_ENDPOINT = "/docs";
    private static final String ROOT_ENDPOINT = "/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRedirectToSwaggerFromRoot() throws Exception {
        // Arrange: se configura la petición GET a la raíz
        
        // Act: se ejecuta la petición
        // Assert: se verifica la redirección hacia Swagger UI
        mockMvc.perform(get(ROOT_ENDPOINT))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION_HEADER, SWAGGER_UI_URL));
    }

    @Test
    void testRedirectToSwaggerFromDocs() throws Exception {
        // Arrange: se configura la petición GET a /docs
        
        // Act: se ejecuta la petición
        // Assert: se verifica la redirección hacia Swagger UI
        mockMvc.perform(get(DOCS_ENDPOINT))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION_HEADER, SWAGGER_UI_URL));
    }

    @Test
    void testRootEndpointReturnsRedirectView() throws Exception {
        // Arrange: se configura la petición a la raíz
        
        // Act: se ejecuta la petición y valida que retorna una RedirectView
        // Assert: verifica que el status es 302 (Found) y la redirección es correcta
        mockMvc.perform(get(ROOT_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SWAGGER_UI_URL));
    }

    @Test
    void testDocsEndpointReturnsRedirectView() throws Exception {
        // Arrange: se configura la petición a /docs
        
        // Act: se ejecuta la petición y valida que retorna una RedirectView
        // Assert: verifica que el status es 302 (Found) y la redirección es correcta
        mockMvc.perform(get(DOCS_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SWAGGER_UI_URL));
    }

    @Test
    void testBothEndpointsRedirectToSameLocation() throws Exception {
        // Arrange: se preparan ambas peticiones
        
        // Act & Assert: se verifica que ambos endpoints redirigen al mismo lugar
        String expectedLocation = SWAGGER_UI_URL;
        
        mockMvc.perform(get(ROOT_ENDPOINT))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION_HEADER, expectedLocation));
                
        mockMvc.perform(get(DOCS_ENDPOINT))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION_HEADER, expectedLocation));
    }
}