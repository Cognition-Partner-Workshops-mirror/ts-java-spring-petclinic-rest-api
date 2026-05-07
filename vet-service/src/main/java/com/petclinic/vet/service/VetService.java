package com.petclinic.vet.service;

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
import java.util.stream.Collectors;

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
    public List<VetResponseDto> findAll() {
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto create(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        Vet updated = vetRepository.save(vet);
        return vetMapper.toResponseDto(updated);
    }

    public VetResponseDto delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName).stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName).stream()
            .map(vetMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    public VetResponseDto assignSpecialties(Integer vetId, List<Integer> specialtyIds) {
        Vet vet = vetRepository.findById(vetId)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", vetId));
        Set<Specialty> specialties = resolveSpecialties(specialtyIds);
        vet.getSpecialties().addAll(specialties);
        Vet updated = vetRepository.save(vet);
        return vetMapper.toResponseDto(updated);
    }

    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Specialty> specialties = specialtyRepository.findAllById(specialtyIds);
        if (specialties.size() != specialtyIds.size()) {
            Set<Integer> foundIds = specialties.stream()
                .map(Specialty::getId)
                .collect(Collectors.toSet());
            List<Integer> notFound = specialtyIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
            throw new ResourceNotFoundException("Specialty", notFound);
        }
        return new HashSet<>(specialties);
    }
}
