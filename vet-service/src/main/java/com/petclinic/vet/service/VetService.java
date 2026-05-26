package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

/**
 * Service interface for veterinarian CRUD operations and search/filter features.
 * Defines the business-logic contract consumed by the controller layer.
 */
public interface VetService {

    /** Returns all vets. */
    List<VetResponseDto> getAllVets();

    /** Finds a single vet by its database ID. */
    VetResponseDto getVetById(Integer id);

    /** Creates a new vet from the given request payload (including specialty assignment). */
    VetResponseDto createVet(VetRequestDto dto);

    /** Updates an existing vet identified by id with the given payload. */
    VetResponseDto updateVet(Integer id, VetRequestDto dto);

    /** Deletes a vet by its ID and returns the deleted record. */
    VetResponseDto deleteVet(Integer id);

    /** Finds all vets that hold the specialty with the given ID. */
    List<VetResponseDto> findBySpecialtyId(Integer specialtyId);

    /** Searches vets by first or last name (case-insensitive partial match). */
    List<VetResponseDto> searchByName(String name);
}
