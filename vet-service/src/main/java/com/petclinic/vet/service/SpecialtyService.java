package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDto> findAll();

    SpecialtyDto findById(Integer id);

    SpecialtyDto create(SpecialtyRequestDto dto);

    SpecialtyDto update(Integer id, SpecialtyRequestDto dto);

    SpecialtyDto delete(Integer id);

    List<SpecialtyDto> searchByName(String name);
}
