package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyService(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> findAll() {
        return specialtyRepository.findAll().stream()
            .map(specialtyMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDto findById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyMapper.updateEntity(dto, specialty);
        Specialty updated = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(updated);
    }

    public SpecialtyResponseDto delete(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(specialty);
        specialtyRepository.delete(specialty);
        return response;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return specialtyRepository.findByNameContainingIgnoreCase(name).stream()
            .map(specialtyMapper::toResponseDto)
            .collect(Collectors.toList());
    }
}
