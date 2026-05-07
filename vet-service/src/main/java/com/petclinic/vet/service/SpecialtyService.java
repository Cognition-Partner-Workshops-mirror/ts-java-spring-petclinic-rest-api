package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponseDto> listSpecialties();

    SpecialtyResponseDto getSpecialty(Integer id);

    SpecialtyResponseDto createSpecialty(SpecialtyRequestDto request);

    SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request);

    SpecialtyResponseDto deleteSpecialty(Integer id);
}
