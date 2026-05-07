package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating or updating a vet.
 * Validation constraints (name patterns, lengths) match the OpenAPI VetFields schema.
 */
public record VetRequest(
    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}$")
    String firstName,

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}\\.?$")
    String lastName,

    @NotNull
    List<SpecialtyResponse> specialties
) {
}
