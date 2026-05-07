package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.VetResponseDto;
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
@Transactional(readOnly = true)
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
    public List<VetResponseDto> findAll() {
        return vetMapper.toResponseDtos(vetRepository.findAll());
    }

    @Override
    public VetResponseDto findById(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    @Transactional
    public VetResponseDto create(VetRequestDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.specialties());
        vet.setSpecialties(resolvedSpecialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    @Transactional
    public VetResponseDto update(int id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        Set<Specialty> resolvedSpecialties = resolveSpecialties(dto.specialties());
        vet.setSpecialties(resolvedSpecialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    @Transactional
    public VetResponseDto delete(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtos(vetRepository.searchByName(name));
    }

    @Override
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseDtos(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequestDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyDtos.stream()
            .map(SpecialtyRequestDto::name)
            .collect(Collectors.toSet());
        List<Specialty> found = specialtyRepository.findByNameInIgnoreCase(names);
        return new HashSet<>(found);
    }
}
