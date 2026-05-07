package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import java.util.List;

public interface VetService {

    List<VetResponseDto> getAllVets();

    VetResponseDto getVetById(Integer id);

    VetResponseDto createVet(VetRequestDto request);

    VetResponseDto updateVet(Integer id, VetRequestDto request);

    VetResponseDto deleteVet(Integer id);

    List<VetResponseDto> searchByName(String name);

    List<VetResponseDto> findBySpecialty(String specialtyName);
}
