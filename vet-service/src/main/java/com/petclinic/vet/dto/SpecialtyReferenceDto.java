package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotNull;

public record SpecialtyReferenceDto(
    @NotNull Integer id,
    String name
) {
}
