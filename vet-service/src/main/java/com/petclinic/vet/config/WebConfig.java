package com.petclinic.vet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS.
 * Allows the Angular frontend (running on port 4200) to call the backend API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Enable CORS for all endpoints.
     * In production, restrict allowedOrigins to the actual frontend domain.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
