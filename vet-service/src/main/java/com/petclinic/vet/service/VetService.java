package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for Vet CRUD operations, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** Retrieve all vets */
    List<VetResponseDto> getAllVets();

    /** Retrieve a single vet by ID */
    VetResponseDto getVetById(Integer id);

    /** Create a new vet with optional specialty assignments */
    VetResponseDto createVet(VetRequestDto request);

    /** Update an existing vet (name and specialty assignments) */
    VetResponseDto updateVet(Integer id, VetRequestDto request);

    /** Delete a vet by ID */
    void deleteVet(Integer id);

    /** Search vets by last name (case-insensitive partial match) */
    List<VetResponseDto> searchByLastName(String lastName);

    /** Filter vets by specialty name (case-insensitive partial match) */
    List<VetResponseDto> filterBySpecialty(String specialtyName);
}
