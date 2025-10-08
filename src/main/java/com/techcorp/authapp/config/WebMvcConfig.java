package com.techcorp.authapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Web MVC para optimizar el manejo de recursos estáticos
 * y mejorar el rendimiento de Swagger UI
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * Configuración de manejadores de recursos estáticos para Swagger
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración específica para Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        // Configuración para recursos de webjars
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        // Configuración para recursos estáticos generales
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
    
    /**
     * Configuración de controladores de vista para redirecciones automáticas
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirección automática de la raíz a Swagger UI
        registry.addRedirectViewController("/", "/swagger-ui.html");
        registry.addRedirectViewController("/docs", "/swagger-ui.html");
        registry.addRedirectViewController("/api", "/swagger-ui.html");
    }
}
