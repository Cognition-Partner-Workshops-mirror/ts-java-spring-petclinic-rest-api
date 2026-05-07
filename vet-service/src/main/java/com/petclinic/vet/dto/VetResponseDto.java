package com.petclinic.vet.dto;

import java.util.List;

/** Outbound DTO returned from Vet endpoints, matching the OpenAPI Vet schema. */
public record VetResponseDto(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyDto> specialties
) {}
