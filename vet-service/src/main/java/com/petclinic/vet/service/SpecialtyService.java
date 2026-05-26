package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for specialty CRUD operations.
 * Defines the business-logic contract consumed by the controller layer.
 */
public interface SpecialtyService {

    /** Returns all specialties. */
    List<SpecialtyResponseDto> getAllSpecialties();

    /** Finds a single specialty by its database ID. */
    SpecialtyResponseDto getSpecialtyById(Integer id);

    /** Creates a new specialty from the given request payload. */
    SpecialtyResponseDto createSpecialty(SpecialtyRequestDto dto);

    /** Updates an existing specialty identified by id with the given payload. */
    SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto dto);

    /** Deletes a specialty by its ID and returns the deleted record. */
    SpecialtyResponseDto deleteSpecialty(Integer id);
}
