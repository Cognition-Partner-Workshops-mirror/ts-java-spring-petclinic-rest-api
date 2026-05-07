package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpecialtyDto(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id,
    String name
) {}
