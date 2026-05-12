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
 * Implementation of {@link VetService}.
 * Handles vet CRUD, specialty assignment via ID lookup, and search/filtering.
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
        List<Vet> vets = vetRepository.findAll();
        return vetMapper.toResponseDtoList(vets);
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
        Vet vet = new Vet(request.getFirstName(), request.getLastName());

        // Resolve specialty IDs to entities and assign to the vet
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialtyIds());
        vet.setSpecialties(specialties);

        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));

        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());

        // Reassign specialties based on the updated list of IDs
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialtyIds());
        vet.setSpecialties(specialties);

        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public void deleteVet(Integer id) {
        if (!vetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vet", id);
        }
        vetRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase(lastName);
        return vetMapper.toResponseDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> filterBySpecialty(Integer specialtyId) {
        // Validate specialty exists before filtering
        if (!specialtyRepository.existsById(specialtyId)) {
            throw new ResourceNotFoundException("Specialty", specialtyId);
        }
        List<Vet> vets = vetRepository.findBySpecialtyId(specialtyId);
        return vetMapper.toResponseDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> filterByLastNameAndSpecialty(String lastName, Integer specialtyId) {
        // Validate specialty exists before filtering
        if (!specialtyRepository.existsById(specialtyId)) {
            throw new ResourceNotFoundException("Specialty", specialtyId);
        }
        List<Vet> vets = vetRepository.findByLastNameAndSpecialtyId(lastName, specialtyId);
        return vetMapper.toResponseDtoList(vets);
    }

    /**
     * Resolve a list of specialty IDs to entity references.
     * Throws ResourceNotFoundException if any ID does not exist.
     */
    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (Integer specialtyId : specialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
            specialties.add(specialty);
        }
        return specialties;
    }
}
