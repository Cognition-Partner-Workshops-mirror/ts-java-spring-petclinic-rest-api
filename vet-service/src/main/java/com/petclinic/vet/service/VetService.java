package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import java.util.List;

public interface VetService {

    List<VetResponseDto> findAll();

    VetResponseDto findById(Integer id);

    VetResponseDto create(VetRequestDto dto);

    VetResponseDto update(Integer id, VetRequestDto dto);

    VetResponseDto delete(Integer id);

    List<VetResponseDto> findBySpecialtyId(Integer specialtyId);

    List<VetResponseDto> findByLastName(String lastName);
}
