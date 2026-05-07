package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
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
import java.util.stream.Collectors;

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
    public List<VetDto> findAll() {
        return vetMapper.toDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto findById(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto update(int id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntity(request, vet);
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toDto(vet);
    }

    @Override
    public void delete(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findByLastName(String lastName) {
        return vetMapper.toDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialty(String specialtyName) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyNameContaining(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> search(String name) {
        return vetMapper.toDtoList(vetRepository.findByNameContaining(name));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyDtos.stream()
            .map(SpecialtyDto::name)
            .collect(Collectors.toSet());
        List<Specialty> found = specialtyRepository.findByNameIn(names);
        return new HashSet<>(found);
    }
}
