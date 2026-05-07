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
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<VetResponseDto> listVets() {
        return vetRepository.findAll().stream()
                .map(vetMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto addVet(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request.getSpecialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet existing = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntity(request, existing);
        existing.setSpecialties(resolveSpecialties(request.getSpecialties()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponseDto(saved);
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
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
                .map(vetMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetRepository.findByNameContaining(name).stream()
                .map(vetMapper::toResponseDto)
                .toList();
    }

    private List<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Specialty> specialties = new ArrayList<>();
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
