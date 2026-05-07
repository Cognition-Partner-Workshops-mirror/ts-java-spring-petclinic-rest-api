package com.petclinic.vet.dto;

/** Response DTO representing a specialty, matching the OpenAPI Specialty schema. */
public record SpecialtyResponse(
    Integer id,
    String name
) {
}
