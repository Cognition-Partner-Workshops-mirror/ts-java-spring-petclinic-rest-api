package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating/updating a Specialty.
 * Matches the Specialty schema from the OpenAPI spec (without the read-only id).
 */
public class SpecialtyRequestDto {

    @NotBlank(message = "Specialty name is required")
    @Size(min = 1, max = 80, message = "Name must be between 1 and 80 characters")
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
