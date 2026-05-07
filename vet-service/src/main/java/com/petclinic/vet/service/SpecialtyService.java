package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for specialty CRUD operations and search.
 */
public interface SpecialtyService {

    /** Retrieve all specialties */
    List<SpecialtyResponseDto> listSpecialties();

    /** Retrieve a specialty by its ID */
    SpecialtyResponseDto getSpecialty(Integer id);

    /** Create a new specialty */
    SpecialtyResponseDto addSpecialty(SpecialtyRequestDto request);

    /** Update an existing specialty */
    SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request);

    /** Delete a specialty by its ID and return the deleted specialty */
    SpecialtyResponseDto deleteSpecialty(Integer id);

    /** Search specialties by name (case-insensitive partial match) */
    List<SpecialtyResponseDto> searchByName(String name);
}
