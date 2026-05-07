package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import java.util.List;

public interface VetService {

    List<VetDto> findAll();

    VetDto findById(Integer id);

    VetDto create(VetRequestDto dto);

    VetDto update(Integer id, VetRequestDto dto);

    VetDto delete(Integer id);

    List<VetDto> findBySpecialtyId(Integer specialtyId);

    List<VetDto> findByLastName(String lastName);

    List<VetDto> findBySpecialtyName(String specialtyName);
}
