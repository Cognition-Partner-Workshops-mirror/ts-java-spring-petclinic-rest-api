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

/**
 * Implementation of VetService with business logic for vet CRUD,
 * specialty assignment, and search/filtering capabilities.
 */
@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> getAllVets() {
        return vetMapper.toResponseDtos(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getVetById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto createVet(VetRequestDto request) {
        Vet vet = new Vet();
        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());
        // Resolve specialties from DB to ensure they exist
        vet.setSpecialties(resolveSpecialties(request.getSpecialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        // Update scalar fields
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        // Replace specialties with resolved set from DB
        existing.clearSpecialties();
        existing.setSpecialties(resolveSpecialties(request.getSpecialties()));
        Vet updated = vetRepository.save(existing);
        return vetMapper.toResponseDto(updated);
    }

    @Override
    public VetResponseDto deleteVet(Integer id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        VetResponseDto response = vetMapper.toResponseDto(existing);
        vetRepository.delete(existing);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findByLastName(String lastName) {
        return vetMapper.toResponseDtos(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toResponseDtos(vetRepository.findBySpecialtyName(specialtyName));
    }

    /**
     * Resolve specialty DTOs to persistent entities.
     * Each specialty in the request must exist in the database (looked up by ID).
     */
    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        Set<Specialty> resolved = new HashSet<>();
        if (specialtyDtos == null) {
            return resolved;
        }
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + dto.getId()));
                resolved.add(specialty);
            }
        }
        return resolved;
    }
}
