package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;

import java.util.List;

/**
 * Service interface for Specialty CRUD operations.
 */
public interface SpecialtyService {

    List<Specialty> findAll();

    Specialty findById(int id);

    Specialty save(SpecialtyDto dto);

    Specialty update(int id, SpecialtyDto dto);

    void delete(int id);
}
