package com.techcorp.authapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controlador para redirecciones y navegación básica
 */
@Controller
public class IndexController {
    
    /**
     * Redirección automática desde la raíz hacia Swagger UI
     */
    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }
    
    /**
     * Redirección alternativa para /docs
     */
    @GetMapping("/docs")
    public RedirectView redirectDocsToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }
}
