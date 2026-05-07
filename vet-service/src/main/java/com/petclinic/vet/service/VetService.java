package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;

import java.util.List;

public interface VetService {

    List<VetDto> findAll();

    VetDto findById(int id);

    VetDto create(VetRequestDto request);

    VetDto update(int id, VetRequestDto request);

    void delete(int id);

    List<VetDto> findByLastName(String lastName);

    List<VetDto> findBySpecialty(String specialtyName);

    List<VetDto> search(String name);
}
