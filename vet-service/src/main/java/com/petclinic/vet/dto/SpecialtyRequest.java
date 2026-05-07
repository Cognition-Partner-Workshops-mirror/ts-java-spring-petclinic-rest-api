package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request DTO for creating or updating a specialty. Validates name per OpenAPI spec constraints. */
public record SpecialtyRequest(
    @NotBlank
    @Size(min = 1, max = 80)
    String name
) {
}
