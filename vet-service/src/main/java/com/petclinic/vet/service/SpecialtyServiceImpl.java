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
 * Default implementation of {@link SpecialtyService}.
 * Delegates persistence to {@link SpecialtyRepository} and mapping to {@link SpecialtyMapper}.
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

    /** Retrieves every specialty in the database, sorted by name. */
    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> getAllSpecialties() {
        return specialtyRepository.findAll()
            .stream()
            .map(specialtyMapper::toResponseDto)
            .toList();
    }

    /** Looks up a specialty by ID; throws 404 if it does not exist. */
    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialtyById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Specialty not found with id: " + id));
        return specialtyMapper.toResponseDto(specialty);
    }

    /** Persists a new specialty and returns the saved record. */
    @Override
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    /** Updates the name of an existing specialty; throws 404 if not found. */
    @Override
    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto dto) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Specialty not found with id: " + id));
        specialtyMapper.updateEntityFromDto(dto, specialty);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    /** Deletes a specialty by ID and returns the deleted record; throws 404 if not found. */
    @Override
    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Specialty not found with id: " + id));
        specialtyRepository.delete(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }
}
