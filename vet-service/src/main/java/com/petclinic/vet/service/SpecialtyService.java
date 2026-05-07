package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;

import java.util.List;

public interface SpecialtyService {

    List<Specialty> findAll();

    Specialty findById(int id);

    Specialty create(SpecialtyRequestDto dto);

    Specialty update(int id, SpecialtyRequestDto dto);

    void delete(int id);
}
