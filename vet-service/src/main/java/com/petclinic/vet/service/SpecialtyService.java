package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDto> findAll();

    SpecialtyDto findById(int id);

    SpecialtyDto create(SpecialtyRequestDto request);

    SpecialtyDto update(int id, SpecialtyRequestDto request);

    void delete(int id);
}
