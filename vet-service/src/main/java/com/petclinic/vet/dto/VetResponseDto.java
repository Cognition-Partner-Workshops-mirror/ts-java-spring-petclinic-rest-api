package com.petclinic.vet.dto;

import java.util.List;

/** Response DTO returned from vet endpoints, includes nested specialty list. */
public record VetResponseDto(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyResponseDto> specialties
) {
}
