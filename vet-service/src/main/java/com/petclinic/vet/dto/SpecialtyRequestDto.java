package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Inbound DTO for creating or updating a specialty.
 * Mirrors the OpenAPI Specialty schema (excluding the read-only id field).
 */
public class SpecialtyRequestDto {

    /** Specialty name, required and between 1-80 characters. */
    @NotBlank(message = "Name is required")
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
