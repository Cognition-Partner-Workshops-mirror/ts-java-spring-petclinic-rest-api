package com.petclinic.vet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Inbound DTO for creating/updating a Vet. Validated with Jakarta constraints. */
public record VetRequestDto(
    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}$")
    String firstName,

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}\\.?$")
    String lastName,

    List<@Valid SpecialtyDto> specialties
) {}
