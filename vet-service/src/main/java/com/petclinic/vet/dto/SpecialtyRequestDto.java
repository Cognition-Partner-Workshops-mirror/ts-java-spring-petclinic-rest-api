package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a specialty.
 * Matches the Specialty schema from the OpenAPI spec (excluding the read-only id field).
 */
public class SpecialtyRequestDto {

    @NotBlank(message = "Specialty name is required")
    @Size(min = 1, max = 80, message = "Specialty name must be between 1 and 80 characters")
    private String name;

    public SpecialtyRequestDto() {
    }

    public SpecialtyRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
