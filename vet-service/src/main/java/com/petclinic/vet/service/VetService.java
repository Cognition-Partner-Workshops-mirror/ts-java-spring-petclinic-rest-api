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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto createVet(VetRequestDto dto) {
        Set<Specialty> specialties = resolveSpecialties(dto.getSpecialties());
        Vet entity = vetMapper.toEntity(dto, specialties);
        entity = vetRepository.save(entity);
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto updateVet(Integer id, VetRequestDto dto) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        Set<Specialty> specialties = resolveSpecialties(dto.getSpecialties());
        vetMapper.updateEntity(entity, dto, specialties);
        entity = vetRepository.save(entity);
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto deleteVet(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(entity);
        vetRepository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName).stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
