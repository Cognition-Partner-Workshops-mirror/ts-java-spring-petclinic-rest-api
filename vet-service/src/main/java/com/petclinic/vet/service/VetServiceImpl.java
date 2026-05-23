package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of VetService handling vet CRUD and specialty resolution.
 */
@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;

    public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vet findById(int id) {
        return vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
    }

    @Override
    public Vet save(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        return vetRepository.save(vet);
    }

    @Override
    public Vet update(int id, VetRequestDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        return vetRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> findBySpecialty(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> searchByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    /**
     * Resolves specialty DTOs to existing entities by name.
     * Throws ResourceNotFoundException if any specialty name is not found.
     */
    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyDtos.stream()
            .map(SpecialtyDto::getName)
            .collect(Collectors.toSet());
        List<Specialty> found = specialtyRepository.findByNameIn(names);

        // Verify all requested specialties were found
        Set<String> foundNames = found.stream()
            .map(Specialty::getName)
            .collect(Collectors.toSet());
        Set<String> notFound = names.stream()
            .filter(name -> foundNames.stream().noneMatch(fn -> fn.equalsIgnoreCase(name)))
            .collect(Collectors.toSet());
        if (!notFound.isEmpty()) {
            throw new ResourceNotFoundException("Specialties not found: " + String.join(", ", notFound));
        }
        return new HashSet<>(found);
    }
}
