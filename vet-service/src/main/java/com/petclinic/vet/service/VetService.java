package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Vet;

import java.util.List;

/**
 * Service interface for Vet domain operations.
 */
public interface VetService {

    List<Vet> findAll();

    Vet findById(int id);

    Vet save(VetRequestDto dto);

    Vet update(int id, VetRequestDto dto);

    void delete(int id);

    List<Vet> findBySpecialty(String specialtyName);

    List<Vet> searchByLastName(String lastName);
}
