package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
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

// Service layer encapsulating business logic for vet CRUD, specialty assignment, and search/filtering
@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vet findById(int id) {
        return vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
    }

    // Creates a new vet, resolving specialty references by name from the database
    public Vet create(VetRequestDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.getSpecialties());
        vet.setSpecialties(resolvedSpecialties);
        return vetRepository.save(vet);
    }

    public Vet update(int id, VetRequestDto dto) {
        Vet existing = findById(id);
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.getSpecialties());
        existing.setSpecialties(resolvedSpecialties);
        return vetRepository.save(existing);
    }

    public void delete(int id) {
        Vet existing = findById(id);
        vetRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<Vet> findBySpecialty(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName);
    }

    @Transactional(readOnly = true)
    public List<Vet> searchByName(String name) {
        return vetRepository.searchByName(name);
    }

    public Vet assignSpecialty(int vetId, int specialtyId) {
        Vet vet = findById(vetId);
        Specialty specialty = specialtyRepository.findById(specialtyId)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
        vet.addSpecialty(specialty);
        return vetRepository.save(vet);
    }

    public Vet removeSpecialty(int vetId, int specialtyId) {
        Vet vet = findById(vetId);
        Specialty specialty = specialtyRepository.findById(specialtyId)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
        vet.removeSpecialty(specialty);
        return vetRepository.save(vet);
    }

    // Resolves specialty DTOs to managed JPA entities by looking up names in the database
    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyDtos.stream()
            .map(SpecialtyResponseDto::getName)
            .collect(Collectors.toSet());
        return new HashSet<>(specialtyRepository.findByNameIn(names));
    }
}
