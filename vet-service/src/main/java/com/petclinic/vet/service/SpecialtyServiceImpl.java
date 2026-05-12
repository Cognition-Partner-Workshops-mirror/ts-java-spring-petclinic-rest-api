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
 * Implementation of {@link SpecialtyService}.
 * Handles CRUD operations and enforces the unique-name business rule.
 */
@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository,
                                SpecialtyMapper specialtyMapper) {
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
        // Enforce unique specialty name
        if (specialtyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Specialty", "name", request.getName());
        }
        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        // Check for name conflict with a different specialty
        specialtyRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Specialty", "name", request.getName());
                    }
                });

        specialtyMapper.updateEntityFromDto(request, specialty);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public void deleteSpecialty(Integer id) {
        if (!specialtyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Specialty", id);
        }
        specialtyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase(name);
        return specialtyMapper.toResponseDtoList(specialties);
    }
}
