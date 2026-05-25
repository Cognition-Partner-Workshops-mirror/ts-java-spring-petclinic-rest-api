package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for Specialty CRUD and search operations.
 */
public interface SpecialtyService {

    /** Retrieve all specialties */
    List<SpecialtyResponseDto> findAll();

    /** Retrieve a specialty by its ID */
    SpecialtyResponseDto findById(Integer id);

    /** Create a new specialty */
    SpecialtyResponseDto create(SpecialtyRequestDto request);

    /** Update an existing specialty */
    SpecialtyResponseDto update(Integer id, SpecialtyRequestDto request);

    /** Delete a specialty by ID */
    void delete(Integer id);

    /** Search specialties by name substring */
    List<SpecialtyResponseDto> searchByName(String name);
}
