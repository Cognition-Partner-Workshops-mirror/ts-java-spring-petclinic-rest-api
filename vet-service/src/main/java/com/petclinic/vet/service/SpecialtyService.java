package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<SpecialtyResponseDto> listSpecialties() {
        return specialtyRepository.findAll().stream()
            .map(specialtyMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialty(Integer id) {
        Specialty entity = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto dto) {
        Specialty entity = specialtyMapper.toEntity(dto);
        entity = specialtyRepository.save(entity);
        return specialtyMapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto dto) {
        Specialty entity = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyMapper.updateEntity(entity, dto);
        entity = specialtyRepository.save(entity);
        return specialtyMapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty entity = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(entity);
        specialtyRepository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return specialtyRepository.findByNameContainingIgnoreCase(name).stream()
            .map(specialtyMapper::toResponseDto)
            .collect(Collectors.toList());
    }
}
