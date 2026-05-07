package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for vet CRUD operations, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** Retrieve all vets */
    List<VetResponseDto> listVets();

    /** Retrieve a vet by its ID */
    VetResponseDto getVet(Integer id);

    /** Create a new vet with the given specialty assignments */
    VetResponseDto addVet(VetRequestDto request);

    /** Update an existing vet, including reassigning specialties */
    VetResponseDto updateVet(Integer id, VetRequestDto request);

    /** Delete a vet by its ID and return the deleted vet */
    VetResponseDto deleteVet(Integer id);

    /** Search vets by last name (case-insensitive partial match) */
    List<VetResponseDto> searchByLastName(String lastName);

    /** Filter vets by specialty ID */
    List<VetResponseDto> filterBySpecialtyId(Integer specialtyId);

    /** Filter vets by specialty name (case-insensitive partial match) */
    List<VetResponseDto> filterBySpecialtyName(String specialtyName);
}
