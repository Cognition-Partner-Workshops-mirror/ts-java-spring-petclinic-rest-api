package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for Vet CRUD operations, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** Retrieve all vets. */
    List<VetResponseDto> getAllVets();

    /** Retrieve a single vet by its ID. */
    VetResponseDto getVetById(Integer id);

    /** Create a new vet with the given specialties. */
    VetResponseDto createVet(VetRequestDto request);

    /** Update an existing vet by ID, replacing specialties. */
    VetResponseDto updateVet(Integer id, VetRequestDto request);

    /** Delete a vet by ID and return the deleted vet. */
    VetResponseDto deleteVet(Integer id);

    /** Find vets by last name (case-insensitive partial match). */
    List<VetResponseDto> findByLastName(String lastName);

    /** Find vets that have a specific specialty by specialty name. */
    List<VetResponseDto> findBySpecialtyName(String specialtyName);
}
