package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponseDto> listAll();

    SpecialtyResponseDto getById(Integer id);

    SpecialtyResponseDto create(SpecialtyRequestDto dto);

    SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto);

    SpecialtyResponseDto delete(Integer id);
}
