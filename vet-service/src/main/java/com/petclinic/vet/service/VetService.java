package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import java.util.List;

public interface VetService {

    List<VetDto> listAll();

    VetDto getById(int id);

    VetDto create(VetRequestDto dto);

    VetDto update(int id, VetRequestDto dto);

    VetDto delete(int id);

    List<VetDto> findBySpecialty(String specialtyName);

    List<VetDto> findByLastName(String lastName);

    List<VetDto> findBySpecialtyAndLastName(String specialtyName, String lastName);
}
