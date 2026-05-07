package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements Vet CRUD, specialty assignment (resolved from DB by name),
 * and search/filter operations.
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
    public List<VetResponseDto> findAll() {
        return vetMapper.toResponseDtos(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public void delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
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
        return vetMapper.toResponseDtos(vetRepository.findByNameContainingIgnoreCase(name));
    }

    // Resolve specialties from DB by name to avoid orphan inserts
    private Set<Specialty> resolveSpecialties(VetRequestDto request) {
        if (request.specialties() == null || request.specialties().isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = request.specialties().stream()
            .map(s -> s.name())
            .collect(Collectors.toSet());
        List<Specialty> found = specialtyRepository.findByNameInIgnoreCase(names);
        return new HashSet<>(found);
    }
}
