package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotNull;

/** Lightweight reference DTO carrying only a specialty ID, used when assigning specialties to a vet. */
public record SpecialtyReferenceDto(
    @NotNull(message = "Specialty ID is required")
    Integer id
) {
}
