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
 * OpenAPI / Swagger UI configuration.
 * Customizes the auto-generated API documentation with service metadata,
 * license info, and server URLs for each environment.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vetServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Vet Microservice API")
                .description("Standalone Spring Boot microservice for managing veterinarians "
                    + "and their specialties. Extracted from the Spring PetClinic domain.")
                .version("1.0.0")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0"))
                .contact(new Contact()
                    .name("PetClinic Team")
                    .url("https://github.com/spring-petclinic")))
            .servers(List.of(
                // Local development server
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server"),
                // Docker Compose server
                new Server()
                    .url("http://localhost:8080")
                    .description("Docker Compose environment")
            ));
    }
}
