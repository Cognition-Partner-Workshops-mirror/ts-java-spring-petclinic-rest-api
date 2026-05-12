package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for specialty CRUD operations.
 */
public interface SpecialtyService {

    /** Retrieve all specialties */
    List<SpecialtyResponseDto> getAllSpecialties();

    /** Retrieve a single specialty by its ID */
    SpecialtyResponseDto getSpecialtyById(Integer id);

    /** Create a new specialty */
    SpecialtyResponseDto createSpecialty(SpecialtyRequestDto request);

    /** Update an existing specialty */
    SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request);

    /** Delete a specialty by its ID */
    void deleteSpecialty(Integer id);

    /** Search specialties by name substring */
    List<SpecialtyResponseDto> searchByName(String name);
}
