package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a Specialty.
 * Contains only the editable fields (excludes id and audit timestamps).
 */
public class SpecialtyRequestDto {

    /** The name of the specialty, required, 1-80 characters. */
    @NotEmpty(message = "Specialty name must not be empty")
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
