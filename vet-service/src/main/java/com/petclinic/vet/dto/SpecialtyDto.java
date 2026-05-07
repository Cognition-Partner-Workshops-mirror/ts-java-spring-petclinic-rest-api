package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for Specialty, matching the OpenAPI Specialty schema.
 * id is read-only (assigned by the server), name is required with 1-80 chars.
 */
public record SpecialtyDto(
    Integer id,

    @NotBlank
    @Size(min = 1, max = 80)
    String name
) {}
