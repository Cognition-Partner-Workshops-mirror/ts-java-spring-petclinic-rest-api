package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;

import java.util.List;

/**
 * Service interface for specialty business logic.
 */
public interface SpecialtyService {

    List<SpecialtyDto> listSpecialties();

    SpecialtyDto getSpecialty(Integer id);

    SpecialtyDto createSpecialty(SpecialtyRequestDto request);

    SpecialtyDto updateSpecialty(Integer id, SpecialtyRequestDto request);

    SpecialtyDto deleteSpecialty(Integer id);

    /**
     * Search specialties whose name contains the given string (case-insensitive).
     */
    List<SpecialtyDto> searchByName(String name);
}
