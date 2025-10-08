package com.techcorp.authapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración completa de Swagger/OpenAPI para el servicio de gestión de usuarios TechCorp
 * Cumple con los lineamientos de documentación y seguridad organizacionales
 */
@Configuration
public class SwaggerConfiguration {
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Value("${spring.application.name:user-management-service}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;
    
    /**
     * Configuración principal de OpenAPI con seguridad JWT y tags organizados
     */
    @Bean
    public OpenAPI TechCorpUserManagementAPI() {
        // Configuración de servidores
        Server developmentServer = new Server()
            .url("http://localhost:" + serverPort)
            .description("Servidor de desarrollo local");
        
        Server testingServer = new Server()
            .url("https://test-api.TechCorp.com")
            .description("Servidor de pruebas");
        
        Server productionServer = new Server()
            .url("https://api.TechCorp.com")
            .description("Servidor de producción");
        
        // Información de contacto
        Contact developmentTeam = new Contact()
            .email("desarrollo@TechCorp.com")
            .name("Equipo de Desarrollo TechCorp")
            .url("https://TechCorp.com");
        
        // Licencia interna
        License TechCorpLicense = new License()
            .name("TechCorp Licencia Interna")
            .url("https://TechCorp.com/licencias");
        
        // Información de la API
        Info apiInfo = new Info()
            .title("TechCorp - API de Gestión de Usuarios")
            .version(applicationVersion)
            .description("Servicio de autenticación y gestión de usuarios para el ecosistema digital TechCorp. " +
                        "Proporciona funcionalidades de registro, autenticación JWT y gestión de perfiles de usuario.")
            .contact(developmentTeam)
            .license(TechCorpLicense);
        
        // Tags para organizar endpoints
        Tag authenticationTag = new Tag()
            .name("Autenticación")
            .description("Operaciones de autenticación de usuarios (login, registro, logout)");
        
        Tag userManagementTag = new Tag()
            .name("Gestión de Usuarios")
            .description("Operaciones de gestión y administración de usuarios");
        
        Tag securityTag = new Tag()
            .name("Seguridad")
            .description("Operaciones relacionadas con tokens y seguridad");
        
        Tag systemTag = new Tag()
            .name("Sistema")
            .description("Información del sistema y verificación de estado");
        
        // Configuración de seguridad JWT
        SecurityScheme jwtSecurityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name("Authorization")
            .description("Token JWT para autenticación. Formato: Bearer {token}");
        
        SecurityRequirement jwtSecurityRequirement = new SecurityRequirement()
            .addList("Bearer Authentication");
        
        // Componentes de seguridad
        Components components = new Components()
            .addSecuritySchemes("Bearer Authentication", jwtSecurityScheme);
        
        return new OpenAPI()
            .info(apiInfo)
            .servers(List.of(developmentServer, testingServer, productionServer))
            .tags(List.of(authenticationTag, userManagementTag, securityTag, systemTag))
            .components(components)
            .addSecurityItem(jwtSecurityRequirement);
    }
    
    /**
     * Configuración agrupada para endpoints de autenticación
     */
    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
            .group("autenticacion")
            .displayName("Autenticación")
            .pathsToMatch("/api/auth/**")
            .build();
    }
    
    /**
     * Configuración agrupada para endpoints de gestión de usuarios
     */
    @Bean
    public GroupedOpenApi userManagementApi() {
        return GroupedOpenApi.builder()
            .group("gestion-usuarios")
            .displayName("Gestión de Usuarios")
            .pathsToMatch("/api/users/**")
            .build();
    }
    
    /**
     * Configuración agrupada para endpoints del sistema
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
            .group("sistema")
            .displayName("Sistema")
            .pathsToMatch("/api/system/**")
            .build();
    }
    
    /**
     * API completa - todos los endpoints
     */
    @Bean
    public GroupedOpenApi completeApi() {
        return GroupedOpenApi.builder()
            .group("completa")
            .displayName("API Completa")
            .pathsToMatch("/api/**")
            .build();
    }
}
