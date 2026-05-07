package com.petclinic.vet.dto;

/** Response DTO returned from specialty endpoints. */
public record SpecialtyResponseDto(
    Integer id,
    String name
) {
}
