package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.entity.VetEntity;
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
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository,
                      SpecialtyRepository specialtyRepository,
                      VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> listAll() {
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public VetResponseDto getById(Integer id) {
        VetEntity entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto create(VetRequestDto dto) {
        VetEntity entity = new VetEntity();
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        entity = vetRepository.save(entity);
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto update(Integer id, VetRequestDto dto) {
        VetEntity entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        entity = vetRepository.save(entity);
        return vetMapper.toResponseDto(entity);
    }

    public VetResponseDto delete(Integer id) {
        VetEntity entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(entity);
        vetRepository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String name) {
        return vetRepository.searchByLastName(name).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    private Set<SpecialtyEntity> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<SpecialtyEntity> specialties = new HashSet<>();
        for (Integer specialtyId : specialtyIds) {
            SpecialtyEntity specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
            specialties.add(specialty);
        }
        return specialties;
    }
}
