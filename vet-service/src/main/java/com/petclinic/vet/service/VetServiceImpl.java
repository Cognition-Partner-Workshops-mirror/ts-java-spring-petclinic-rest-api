package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
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

import java.util.ArrayList;
import java.util.List;

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
    public List<VetResponseDto> findAll() {
        List<Vet> vets = vetRepository.findAll();
        return vetMapper.toResponseDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        List<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntityFromDto(request, vet);
        List<Specialty> specialties = resolveSpecialties(request.getSpecialties());
        vet.setSpecialties(specialties);
        Vet updated = vetRepository.save(vet);
        return vetMapper.toResponseDto(updated);
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
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        List<Vet> vets = vetRepository.findBySpecialtyId(specialtyId);
        return vetMapper.toResponseDtoList(vets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        List<Vet> vets = vetRepository.searchByName(name);
        return vetMapper.toResponseDtoList(vets);
    }

    private List<Specialty> resolveSpecialties(List<SpecialtyRequestDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Specialty> resolved = new ArrayList<>();
        for (SpecialtyRequestDto dto : specialtyDtos) {
            Specialty specialty = specialtyRepository.findByNameIgnoreCase(dto.getName())
                .orElseGet(() -> {
                    Specialty newSpecialty = new Specialty();
                    newSpecialty.setName(dto.getName());
                    return specialtyRepository.save(newSpecialty);
                });
            resolved.add(specialty);
        }
        return resolved;
    }
}
