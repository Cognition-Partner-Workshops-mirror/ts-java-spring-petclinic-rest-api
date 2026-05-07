package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyDto(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id,

    @NotBlank
    @Size(min = 1, max = 80)
    String name
) {}
