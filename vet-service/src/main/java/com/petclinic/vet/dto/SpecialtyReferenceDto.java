package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotNull;

public record SpecialtyReferenceDto(
    @NotNull(message = "Specialty ID is required")
    Integer id,
    String name
) {
}
