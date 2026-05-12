package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for vet CRUD operations, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** Retrieve all vets */
    List<VetResponseDto> getAllVets();

    /** Retrieve a single vet by ID */
    VetResponseDto getVetById(Integer id);

    /** Create a new vet with optional specialty assignment */
    VetResponseDto createVet(VetRequestDto request);

    /** Update an existing vet and reassign specialties */
    VetResponseDto updateVet(Integer id, VetRequestDto request);

    /** Delete a vet by ID */
    void deleteVet(Integer id);

    /** Filter vets by last name substring */
    List<VetResponseDto> searchByLastName(String lastName);

    /** Filter vets by specialty ID */
    List<VetResponseDto> filterBySpecialty(Integer specialtyId);

    /** Combined filter: last name substring + specialty ID */
    List<VetResponseDto> filterByLastNameAndSpecialty(String lastName, Integer specialtyId);
}
