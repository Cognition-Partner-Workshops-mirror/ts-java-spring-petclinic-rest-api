package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record VetDto(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyDto> specialties
) {}
