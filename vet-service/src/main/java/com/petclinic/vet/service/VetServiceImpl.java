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
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponseDto)
            .toList();
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
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.firstName());
        existing.setLastName(dto.lastName());
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        existing.setSpecialties(specialties);
        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!vetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vet", id);
        }
        vetRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetRepository.findByNameContainingIgnoreCase(name).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return specialties;
        }
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.id() != null) {
                Specialty specialty = specialtyRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
