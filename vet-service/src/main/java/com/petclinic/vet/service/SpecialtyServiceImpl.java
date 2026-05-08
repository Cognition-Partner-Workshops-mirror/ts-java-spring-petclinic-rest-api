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

/**
 * Implementation of SpecialtyService with business logic for specialty CRUD.
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
        return specialtyMapper.toResponseDtos(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialtyById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto request) {
        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        // Update fields from the request DTO onto the existing entity
        specialtyMapper.updateEntity(request, existing);
        Specialty updated = specialtyRepository.save(existing);
        return specialtyMapper.toResponseDto(updated);
    }

    @Override
    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(existing);
        specialtyRepository.delete(existing);
        return response;
    }
}
