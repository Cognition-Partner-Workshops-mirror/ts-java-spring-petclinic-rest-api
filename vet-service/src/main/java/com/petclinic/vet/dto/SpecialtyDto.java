package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO for Specialty used in both request (nested in VetRequestDto) and response payloads. */
public record SpecialtyDto(
    Integer id,
    @NotBlank @Size(min = 1, max = 80) String name
) {}
