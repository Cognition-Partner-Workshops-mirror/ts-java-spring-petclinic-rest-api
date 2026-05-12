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
 * Implementation of VetService with business logic for vet CRUD and specialty assignment.
 * Resolves specialty references from the request DTO and manages the many-to-many relationship.
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
        Vet vet = vetMapper.toEntity(request);
        // Resolve and assign specialties from the request
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntityFromDto(request, vet);
        // Resolve and reassign specialties
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(specialties);
        Vet updated = vetRepository.save(vet);
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
        List<Vet> vets = vetRepository.searchByLastName(lastName);
        return vetMapper.toResponseDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        List<Vet> vets = vetRepository.findBySpecialtyName(specialtyName);
        return vetMapper.toResponseDtoList(vets);
    }

    /**
     * Resolve specialty DTOs from the request into managed JPA entities.
     * Each specialty must exist in the database; throws ResourceNotFoundException if not found.
     */
    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos != null) {
            for (SpecialtyResponseDto dto : specialtyDtos) {
                if (dto.getId() != null) {
                    Specialty specialty = specialtyRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                    specialties.add(specialty);
                }
            }
        }
        return specialties;
    }
}
