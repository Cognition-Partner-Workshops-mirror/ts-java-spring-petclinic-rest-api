package com.petclinic.vet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for Vet, matching the OpenAPI Vet schema (VetFields + id).
 * Validation constraints mirror the OpenAPI spec: name patterns, size limits, required specialties.
 */
public record VetDto(
    Integer id,

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}$")
    String firstName,

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}\\.?$")
    String lastName,

    @NotNull
    List<@Valid SpecialtyDto> specialties
) {}
