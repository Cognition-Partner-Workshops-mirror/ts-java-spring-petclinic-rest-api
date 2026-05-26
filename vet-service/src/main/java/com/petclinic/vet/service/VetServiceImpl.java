package com.petclinic.vet.service;

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
 * Default implementation of {@link VetService}.
 * Handles vet CRUD, specialty assignment, and search/filtering logic.
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

    /** Retrieves every vet in the database. */
    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> getAllVets() {
        return vetRepository.findAll()
            .stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    /** Looks up a vet by ID; throws 404 if it does not exist. */
    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getVetById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vet not found with id: " + id));
        return vetMapper.toResponseDto(vet);
    }

    /**
     * Creates a new vet and assigns the specialties identified by the IDs in the request.
     * Throws 404 if any of the referenced specialty IDs do not exist.
     */
    @Override
    public VetResponseDto createVet(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    /**
     * Updates an existing vet's name and specialties.
     * Throws 404 if the vet or any referenced specialty does not exist.
     */
    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vet not found with id: " + id));
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    /** Deletes a vet by ID and returns the deleted record; throws 404 if not found. */
    @Override
    public VetResponseDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vet not found with id: " + id));
        vetRepository.delete(vet);
        return vetMapper.toResponseDto(vet);
    }

    /** Returns all vets that hold the given specialty. */
    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId)
            .stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    /** Searches vets by first or last name (case-insensitive partial match). */
    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetRepository.searchByName(name)
            .stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    /**
     * Resolves a list of specialty IDs to their JPA entities.
     * Throws {@link ResourceNotFoundException} if any ID is not found.
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
