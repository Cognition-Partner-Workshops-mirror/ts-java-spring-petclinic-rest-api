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
    public List<VetResponseDto> findAll() {
        return vetMapper.toResponseDtos(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        vet.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto delete(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseDtos(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtos(vetRepository.findByNameContainingIgnoreCase(name));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> resolved = new HashSet<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                resolved.add(specialty);
            }
        }
        return resolved;
    }
}
