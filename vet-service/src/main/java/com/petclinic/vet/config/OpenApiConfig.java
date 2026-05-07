package com.petclinic.vet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vetServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vet Service API")
                        .description("Standalone microservice for managing veterinarians and specialties")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PetClinic Team")
                                .email("team@petclinic.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/petclinic/api").description("Default server")));
    }
}
