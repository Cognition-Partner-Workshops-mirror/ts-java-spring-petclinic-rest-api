package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;

import java.util.List;

/** Service interface defining CRUD and search operations for Vet resources. */
public interface VetService {

    List<VetDto> findAll();

    VetDto findById(Integer id);

    VetDto create(VetDto dto);

    VetDto update(Integer id, VetDto dto);

    VetDto delete(Integer id);

    List<VetDto> findByLastName(String lastName);

    List<VetDto> findBySpecialtyName(String specialtyName);

    List<VetDto> findByLastNameAndSpecialtyName(String lastName, String specialtyName);
}
