package com.petclinic.vet.dto;

import java.util.List;

public record VetResponse(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyResponse> specialties
) {}
