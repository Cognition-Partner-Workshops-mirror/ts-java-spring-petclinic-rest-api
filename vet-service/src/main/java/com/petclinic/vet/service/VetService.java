package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for Vet CRUD, specialty assignment, and search/filtering.
 */
public interface VetService {

    /** Retrieve all vets */
    List<VetResponseDto> findAll();

    /** Retrieve a vet by its ID */
    VetResponseDto findById(Integer id);

    /** Create a new vet with optional specialty assignments */
    VetResponseDto create(VetRequestDto request);

    /** Update an existing vet including specialty reassignment */
    VetResponseDto update(Integer id, VetRequestDto request);

    /** Delete a vet by ID */
    void delete(Integer id);

    /** Search vets by last name substring */
    List<VetResponseDto> searchByLastName(String lastName);

    /** Filter vets by specialty name */
    List<VetResponseDto> findBySpecialtyName(String specialtyName);
}
