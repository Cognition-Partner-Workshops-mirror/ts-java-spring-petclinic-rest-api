package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
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
 * Transactional implementation of VetService.
 * Handles vet CRUD, specialty assignment (resolving specialty IDs), and search/filtering.
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
    public List<VetDto> findAll() {
        return vetMapper.toDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto update(Integer id, VetDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.firstName());
        existing.setLastName(dto.lastName());
        existing.setSpecialties(resolveSpecialties(dto.specialties()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto delete(Integer id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetDto dto = vetMapper.toDto(existing);
        vetRepository.delete(existing);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findByLastName(String lastName) {
        return vetMapper.toDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findByLastNameAndSpecialtyName(String lastName, String specialtyName) {
        return vetMapper.toDtoList(
            vetRepository.findByLastNameAndSpecialtyName(lastName, specialtyName));
    }

    /** Resolve specialty DTOs to managed JPA entities by their IDs. */
    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos != null) {
            for (SpecialtyDto dto : specialtyDtos) {
                if (dto.id() != null) {
                    Specialty specialty = specialtyRepository.findById(dto.id())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                    specialties.add(specialty);
                }
            }
        }
        return specialties;
    }
}
