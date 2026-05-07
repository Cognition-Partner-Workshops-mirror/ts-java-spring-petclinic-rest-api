package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDto> listAll();

    SpecialtyDto getById(int id);

    SpecialtyDto create(SpecialtyRequestDto dto);

    SpecialtyDto update(int id, SpecialtyRequestDto dto);

    SpecialtyDto delete(int id);
}
