package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.VetResponseDto;

import java.util.List;

public interface VetService {

    List<VetResponseDto> findAll();

    VetResponseDto findById(int id);

    VetResponseDto create(VetRequestDto dto);

    VetResponseDto update(int id, VetRequestDto dto);

    VetResponseDto delete(int id);

    List<VetResponseDto> searchByName(String name);

    List<VetResponseDto> findBySpecialty(String specialtyName);
}
