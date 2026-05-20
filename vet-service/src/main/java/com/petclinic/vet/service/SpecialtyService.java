package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

/**
 * Service interface for Specialty CRUD operations and search.
 */
public interface SpecialtyService {

    /** List all specialties. */
    List<SpecialtyResponseDto> findAll();

    /** Find a specialty by ID, throws ResourceNotFoundException if not found. */
    SpecialtyResponseDto findById(Integer id);

    /** Create a new specialty. */
    SpecialtyResponseDto create(SpecialtyRequestDto request);

    /** Update an existing specialty, throws ResourceNotFoundException if not found. */
    SpecialtyResponseDto update(Integer id, SpecialtyRequestDto request);

    /** Delete a specialty by ID, throws ResourceNotFoundException if not found. */
    void delete(Integer id);

    /** Search specialties by name (case-insensitive partial match). */
    List<SpecialtyResponseDto> searchByName(String name);
}
