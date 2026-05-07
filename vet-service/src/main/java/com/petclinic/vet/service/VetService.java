package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto createVet(VetRequestDto request) {
        Vet vet = new Vet();
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(Integer specialtyId) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        return vetMapper.toResponseDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.id() != null) {
                Specialty specialty = specialtyRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
