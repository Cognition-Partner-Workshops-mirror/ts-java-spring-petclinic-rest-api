package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;

import java.util.List;

/**
 * Service interface for veterinarian business logic.
 */
public interface VetService {

    List<VetDto> listVets();

    VetDto getVet(Integer id);

    VetDto createVet(VetRequestDto request);

    VetDto updateVet(Integer id, VetRequestDto request);

    VetDto deleteVet(Integer id);

    /**
     * Find all vets that have a given specialty (by specialty ID).
     */
    List<VetDto> findBySpecialty(Integer specialtyId);

    /**
     * Search vets whose last name contains the given string (case-insensitive).
     */
    List<VetDto> searchByLastName(String lastName);
}
