package com.petclinic.vet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration for the Vet microservice.
 * Accessible at /swagger-ui.html when the application is running.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Defines the OpenAPI metadata displayed in Swagger UI,
     * including service description, version, contact, and license info.
     */
    @Bean
    public OpenAPI vetServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Vet Service API")
                .description("REST API for managing veterinarians and specialties, "
                    + "extracted from the Spring PetClinic monolith.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("PetClinic Team")
                    .url("https://github.com/spring-petclinic"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local development")));
    }
}
