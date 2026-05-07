package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;

import java.util.List;

public interface VetService {

    List<VetResponseDto> listVets();

    VetResponseDto getVet(Integer id);

    VetResponseDto createVet(VetRequestDto request);

    VetResponseDto updateVet(Integer id, VetRequestDto request);

    VetResponseDto deleteVet(Integer id);

    List<VetResponseDto> findBySpecialtyId(Integer specialtyId);

    List<VetResponseDto> findByLastName(String lastName);

    List<VetResponseDto> findBySpecialtyName(String specialtyName);
}
