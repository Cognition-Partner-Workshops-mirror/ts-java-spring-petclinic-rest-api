package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for Vet CRUD operations, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** List all vets. */
    List<VetResponseDto> findAll();

    /** Find a vet by ID, throws ResourceNotFoundException if not found. */
    VetResponseDto findById(Integer id);

    /** Create a new vet with optional specialty assignments. */
    VetResponseDto create(VetRequestDto request);

    /** Update an existing vet, throws ResourceNotFoundException if not found. */
    VetResponseDto update(Integer id, VetRequestDto request);

    /** Delete a vet by ID, throws ResourceNotFoundException if not found. */
    void delete(Integer id);

    /** Filter vets by specialty name (case-insensitive exact match). */
    List<VetResponseDto> findBySpecialty(String specialtyName);

    /** Search vets by first or last name (case-insensitive partial match). */
    List<VetResponseDto> searchByName(String name);
}
