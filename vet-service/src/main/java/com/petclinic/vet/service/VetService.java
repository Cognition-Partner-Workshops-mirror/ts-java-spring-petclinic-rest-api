package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import java.util.List;

public interface VetService {

    List<VetDto> findAll();

    VetDto findById(Integer id);

    VetDto create(VetDto dto);

    VetDto update(Integer id, VetDto dto);

    void delete(Integer id);

    List<VetDto> findBySpecialtyId(Integer specialtyId);

    List<VetDto> findByLastName(String lastName);
}
