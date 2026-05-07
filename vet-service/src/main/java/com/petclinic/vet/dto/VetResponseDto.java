package com.petclinic.vet.dto;

import java.util.List;

public record VetResponseDto(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyResponseDto> specialties
) {
}
