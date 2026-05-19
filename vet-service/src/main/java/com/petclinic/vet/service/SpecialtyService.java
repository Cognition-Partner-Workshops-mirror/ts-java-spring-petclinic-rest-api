package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for Specialty CRUD operations and search.
 */
public interface SpecialtyService {

    /** Retrieve all specialties */
    List<SpecialtyResponseDto> getAllSpecialties();

    /** Retrieve a single specialty by ID */
    SpecialtyResponseDto getSpecialtyById(Integer id);

    /** Create a new specialty */
    SpecialtyResponseDto createSpecialty(SpecialtyRequestDto request);

    /** Update an existing specialty */
    SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request);

    /** Delete a specialty by ID */
    void deleteSpecialty(Integer id);
}
