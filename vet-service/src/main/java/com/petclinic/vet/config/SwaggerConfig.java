package com.petclinic.vet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the Vet microservice.
 * Modeled after the monolith's SwaggerConfig, adapted for the standalone vet-service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("Vet Service API")
                .version("1.0")
                .description("REST API for the Vet microservice — manages veterinarians and specialties")
                .license(swaggerLicense())
                .contact(swaggerContact()));
    }

    private Contact swaggerContact() {
        Contact contact = new Contact();
        contact.setName("PetClinic Team");
        contact.setUrl("https://spring-petclinic.github.io/");
        return contact;
    }

    private License swaggerLicense() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0");
        license.setExtensions(Collections.emptyMap());
        return license;
    }
}
