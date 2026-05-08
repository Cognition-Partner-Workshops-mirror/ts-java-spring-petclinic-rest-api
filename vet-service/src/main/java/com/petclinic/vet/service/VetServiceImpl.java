package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of VetService.
 * Handles CRUD, specialty assignment, and search/filtering for veterinarians.
 */
@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository,
                          SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> getAllVets() {
        return vetRepository.findAll()
            .stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getVetById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto createVet(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        // Resolve and assign specialties from the request
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntityFromDto(request, existing);
        // Resolve and re-assign specialties
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        existing.setSpecialties(specialties);
        Vet updated = vetRepository.save(existing);
        return vetMapper.toResponseDto(updated);
    }

    @Override
    public VetResponseDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName)
            .stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> filterBySpecialty(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId)
            .stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * Resolve specialty entities from the DTO list.
     * Each specialty must already exist in the database (looked up by ID).
     */
    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
