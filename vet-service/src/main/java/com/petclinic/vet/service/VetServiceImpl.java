package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
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

/**
 * Implementation of veterinarian business logic.
 * Handles CRUD operations, specialty assignment, and search/filtering for vets.
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
    public List<VetDto> listVets() {
        return vetMapper.toDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto createVet(VetRequestDto request) {
        Vet vet = new Vet();
        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());
        // Resolve specialty IDs to entities and assign to the vet
        vet.setSpecialties(resolveSpecialties(request.getSpecialtyIds()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto updateVet(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());
        // Replace all specialties with the new set from the request
        vet.setSpecialties(resolveSpecialties(request.getSpecialtyIds()));
        Vet updated = vetRepository.save(vet);
        return vetMapper.toDto(updated);
    }

    @Override
    public VetDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        vetRepository.delete(vet);
        return vetMapper.toDto(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialty(Integer specialtyId) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> searchByLastName(String lastName) {
        return vetMapper.toDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    /**
     * Resolves a list of specialty IDs to a set of Specialty entities.
     * Throws ResourceNotFoundException if any ID does not exist.
     */
    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (Integer specialtyId : specialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Specialty not found with id: " + specialtyId));
            specialties.add(specialty);
        }
        return specialties;
    }
}
