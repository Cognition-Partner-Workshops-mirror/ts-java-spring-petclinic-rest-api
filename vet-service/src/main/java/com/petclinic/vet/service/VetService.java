package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

public interface VetService {

    List<VetResponseDto> findAll();

    VetResponseDto findById(Integer id);

    VetResponseDto create(VetRequestDto request);

    VetResponseDto update(Integer id, VetRequestDto request);

    void delete(Integer id);

    List<VetResponseDto> findBySpecialtyName(String specialtyName);

    List<VetResponseDto> findByLastName(String lastName);
}
