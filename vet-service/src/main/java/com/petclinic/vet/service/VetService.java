package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import java.util.List;

public interface VetService {

    List<VetResponseDto> listAll();

    VetResponseDto getById(Integer id);

    VetResponseDto create(VetRequestDto dto);

    VetResponseDto update(Integer id, VetRequestDto dto);

    VetResponseDto delete(Integer id);

    List<VetResponseDto> findBySpecialty(String specialtyName);

    List<VetResponseDto> searchByName(String name);
}
