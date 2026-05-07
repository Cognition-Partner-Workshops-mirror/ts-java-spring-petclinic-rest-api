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
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto createVet(VetRequestDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        vet.setSpecialties(resolveSpecialties(dto.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto updateVet(Integer id, VetRequestDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntity(dto, existing);
        existing.setSpecialties(resolveSpecialties(dto.specialties()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto deleteVet(Integer id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(existing);
        return vetMapper.toResponseDto(existing);
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtoList(vetRepository.findByNameContaining(name));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            Specialty specialty = specialtyRepository.findById(dto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
            specialties.add(specialty);
        }
        return specialties;
    }
}
