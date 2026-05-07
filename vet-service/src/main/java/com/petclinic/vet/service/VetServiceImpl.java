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

    public VetServiceImpl(VetRepository vetRepository,
                          SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> listAll() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
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
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findByLastName(String lastName) {
        return vetMapper.toResponseDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos != null) {
            for (SpecialtyResponseDto dto : specialtyDtos) {
                Specialty specialty = specialtyRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
