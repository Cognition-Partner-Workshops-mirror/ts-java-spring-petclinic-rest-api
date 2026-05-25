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
 * Handles business logic for specialty CRUD operations with duplicate-name checks.
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
    public List<SpecialtyResponseDto> findAll() {
        return specialtyMapper.toResponseDtoList(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto findById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto create(SpecialtyRequestDto request) {
        // Check for duplicate name before creating
        specialtyRepository.findByNameIgnoreCase(request.getName())
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    String.format("Specialty with name '%s' already exists", request.getName()));
            });
        Specialty entity = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(entity);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto request) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        // Check for duplicate name (excluding the current entity)
        specialtyRepository.findByNameIgnoreCase(request.getName())
            .ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new DuplicateResourceException(
                        String.format("Specialty with name '%s' already exists", request.getName()));
                }
            });

        specialtyMapper.updateEntityFromDto(request, existing);
        Specialty saved = specialtyRepository.save(existing);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyRepository.delete(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return specialtyMapper.toResponseDtoList(
            specialtyRepository.findByNameContainingIgnoreCase(name));
    }
}
