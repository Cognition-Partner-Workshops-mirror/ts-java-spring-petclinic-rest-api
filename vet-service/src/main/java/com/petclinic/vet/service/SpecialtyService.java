package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<SpecialtyResponse> listSpecialties() {
        return specialtyRepository.findAll().stream()
            .map(specialtyMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SpecialtyResponse getSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponse(specialty);
    }

    public SpecialtyResponse createSpecialty(SpecialtyRequest request) {
        if (specialtyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                String.format("Specialty with name '%s' already exists", request.getName()));
        }
        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponse(saved);
    }

    public SpecialtyResponse updateSpecialty(Integer id, SpecialtyRequest request) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        specialtyRepository.findByNameIgnoreCase(request.getName())
            .filter(s -> !s.getId().equals(id))
            .ifPresent(s -> {
                throw new DuplicateResourceException(
                    String.format("Specialty with name '%s' already exists", request.getName()));
            });

        specialtyMapper.updateEntity(request, existing);
        Specialty saved = specialtyRepository.save(existing);
        return specialtyMapper.toResponse(saved);
    }

    public SpecialtyResponse deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponse response = specialtyMapper.toResponse(specialty);
        specialtyRepository.delete(specialty);
        return response;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> searchByName(String name) {
        return specialtyRepository.searchByName(name).stream()
            .map(specialtyMapper::toResponse)
            .toList();
    }
}
