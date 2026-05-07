package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyRequest(
    @NotBlank
    @Size(min = 1, max = 80)
    String name
) {}
