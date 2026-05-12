package com.petclinic.vet.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for OpenApiConfig to verify Swagger UI metadata.
 */
class OpenApiConfigTest {

    @Test
    void vetServiceOpenAPI_returnsConfiguredOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.vetServiceOpenAPI();

        // Verify API info metadata is set correctly
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Vet Microservice API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("Apache 2.0");
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("PetClinic Team");
        assertThat(openAPI.getServers()).hasSize(2);
    }
}
