package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a specialty.
 * Contains only editable fields (no id).
 */
public class SpecialtyRequestDto {

    @NotEmpty(message = "Name must not be empty")
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
