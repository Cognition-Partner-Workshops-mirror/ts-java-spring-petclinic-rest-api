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
 * Handles vet CRUD, specialty assignment (resolving by name), and search/filtering.
 */
@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findAll() {
        return vetMapper.toResponseDtos(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        // Resolve specialty references by name from the database
        Set<Specialty> resolvedSpecialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(resolvedSpecialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto request) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        // Update editable fields
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        // Re-resolve specialties from the request
        Set<Specialty> resolvedSpecialties = resolveSpecialties(request.getSpecialties());
        existing.setSpecialties(resolvedSpecialties);
        Vet updated = vetRepository.save(existing);
        return vetMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        vetRepository.delete(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseDtos(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtos(vetRepository.findByNameContaining(name));
    }

    /**
     * Resolve specialty DTOs to managed JPA entities by looking up names in the database.
     * Specialties are matched by name (case-insensitive); unrecognized names are ignored.
     */
    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyDtos.stream()
            .map(s -> s.getName().toLowerCase())
            .collect(Collectors.toSet());
        return new HashSet<>(specialtyRepository.findByNameInIgnoreCase(names));
    }
}
