package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for vet business operations.
 */
public interface VetService {

    /**
     * Retrieve all vets.
     */
    List<VetResponseDto> getAllVets();

    /**
     * Retrieve a vet by its ID.
     */
    VetResponseDto getVetById(Integer id);

    /**
     * Create a new vet with associated specialties.
     */
    VetResponseDto createVet(VetRequestDto request);

    /**
     * Update an existing vet, including specialty assignment.
     */
    VetResponseDto updateVet(Integer id, VetRequestDto request);

    /**
     * Delete a vet by its ID, returning the deleted vet data.
     */
    VetResponseDto deleteVet(Integer id);

    /**
     * Search vets by last name (partial, case-insensitive).
     */
    List<VetResponseDto> searchByLastName(String lastName);

    /**
     * Filter vets by specialty ID.
     */
    List<VetResponseDto> filterBySpecialty(Integer specialtyId);
}
