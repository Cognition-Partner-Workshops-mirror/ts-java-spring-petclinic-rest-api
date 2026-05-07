package com.petclinic.vet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyRequestDto(
    Integer id,

    @NotBlank
    @Size(min = 1, max = 80)
    String name
) {}
