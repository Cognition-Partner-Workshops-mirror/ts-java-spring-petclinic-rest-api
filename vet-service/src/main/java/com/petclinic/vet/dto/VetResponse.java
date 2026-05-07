package com.petclinic.vet.dto;

import java.util.List;

/** Response DTO representing a vet with their specialties, matching the OpenAPI Vet schema. */
public record VetResponse(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyResponse> specialties
) {
}
