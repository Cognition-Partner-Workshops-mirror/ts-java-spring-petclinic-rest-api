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
 * OpenAPI/Swagger UI configuration for the Vet Service.
 * Provides API metadata displayed in the Swagger UI documentation page.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification metadata for the Vet Service API.
     * Accessible at /api/swagger-ui.html when the service is running.
     */
    @Bean
    public OpenAPI vetServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Vet Service API")
                .description("Standalone microservice for managing veterinarians and their specialties. "
                    + "Provides CRUD operations for vets and specialties with many-to-many relationship support.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("PetClinic Team")
                    .url("https://github.com/spring-petclinic"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8083/api")
                    .description("Local development server"),
                new Server()
                    .url("http://vet-service:8083/api")
                    .description("Docker Compose environment")
            ));
    }
}
