package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponseDto> findAll();

    SpecialtyResponseDto findById(int id);

    SpecialtyResponseDto create(SpecialtyRequestDto dto);

    SpecialtyResponseDto update(int id, SpecialtyRequestDto dto);

    SpecialtyResponseDto delete(int id);
}
