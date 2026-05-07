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

@Service
@Transactional(readOnly = true)
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyService(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    public List<SpecialtyResponseDto> findAll() {
        return specialtyRepository.findAll().stream()
            .map(specialtyMapper::toResponseDto)
            .toList();
    }

    public SpecialtyResponseDto findById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Transactional
    public SpecialtyResponseDto create(SpecialtyRequestDto request) {
        if (specialtyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(
                "Specialty with name '" + request.name() + "' already exists");
        }
        Specialty specialty = new Specialty(request.name());
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    @Transactional
    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto request) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        specialtyRepository.findByNameIgnoreCase(request.name())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    "Specialty with name '" + request.name() + "' already exists");
            });

        specialty.setName(request.name());
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    @Transactional
    public SpecialtyResponseDto delete(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyRepository.delete(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }
}
