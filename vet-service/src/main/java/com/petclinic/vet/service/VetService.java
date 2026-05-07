package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import java.util.List;

public interface VetService {

    List<VetResponseDto> listAll();

    VetResponseDto getById(Integer id);

    VetResponseDto create(VetRequestDto request);

    VetResponseDto update(Integer id, VetRequestDto request);

    VetResponseDto delete(Integer id);

    List<VetResponseDto> findBySpecialtyId(Integer specialtyId);

    List<VetResponseDto> findByLastName(String lastName);

    List<VetResponseDto> findBySpecialtyName(String specialtyName);
}
