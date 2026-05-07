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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper mapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper mapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> listAll() {
        return vetRepository.findAll().stream()
            .map(mapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public VetResponseDto getById(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return mapper.toResponseDto(entity);
    }

    public VetResponseDto create(VetRequestDto dto) {
        Vet entity = mapper.toEntity(dto);
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        entity.setSpecialties(specialties);
        Vet saved = vetRepository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        entity.setSpecialties(specialties);
        Vet saved = vetRepository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public VetResponseDto delete(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = mapper.toResponseDto(entity);
        vetRepository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetRepository.findByNameContaining(name).stream()
            .map(mapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> filterBySpecialty(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
            .map(mapper::toResponseDto)
            .toList();
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> result = new HashSet<>();
        for (SpecialtyResponseDto dto : dtos) {
            if (dto.id() != null) {
                Specialty specialty = specialtyRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                result.add(specialty);
            }
        }
        return result;
    }
}
