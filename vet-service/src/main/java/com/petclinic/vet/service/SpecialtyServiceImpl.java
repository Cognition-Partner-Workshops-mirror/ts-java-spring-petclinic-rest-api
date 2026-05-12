package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of SpecialtyService with business logic for specialty CRUD.
 * Enforces uniqueness of specialty names and handles entity lifecycle.
 */
@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        return specialtyMapper.toResponseDtoList(specialties);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialtyById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto request) {
        // Check for duplicate specialty name
        if (specialtyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                String.format("Specialty with name '%s' already exists", request.getName()));
        }
        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        // Check for duplicate name (excluding current specialty)
        List<Specialty> existing = specialtyRepository.findByNameIgnoreCase(request.getName());
        boolean duplicateExists = existing.stream().anyMatch(s -> !s.getId().equals(id));
        if (duplicateExists) {
            throw new DuplicateResourceException(
                String.format("Specialty with name '%s' already exists", request.getName()));
        }

        specialtyMapper.updateEntityFromDto(request, specialty);
        Specialty updated = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(updated);
    }

    @Override
    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(specialty);
        specialtyRepository.delete(specialty);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        List<Specialty> specialties = specialtyRepository.searchByName(name);
        return specialtyMapper.toResponseDtoList(specialties);
    }
}
