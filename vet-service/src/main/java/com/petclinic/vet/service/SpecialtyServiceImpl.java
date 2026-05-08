package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of specialty business logic.
 * Handles CRUD operations and name-based search for specialties.
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
    public List<SpecialtyDto> listSpecialties() {
        return specialtyMapper.toDtoList(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto getSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        return specialtyMapper.toDto(specialty);
    }

    @Override
    public SpecialtyDto createSpecialty(SpecialtyRequestDto request) {
        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toDto(saved);
    }

    @Override
    public SpecialtyDto updateSpecialty(Integer id, SpecialtyRequestDto request) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        // Update fields from request DTO onto the existing entity
        specialtyMapper.updateEntity(request, specialty);
        Specialty updated = specialtyRepository.save(specialty);
        return specialtyMapper.toDto(updated);
    }

    @Override
    public SpecialtyDto deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        specialtyRepository.delete(specialty);
        return specialtyMapper.toDto(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto> searchByName(String name) {
        return specialtyMapper.toDtoList(specialtyRepository.findByNameContainingIgnoreCase(name));
    }
}
