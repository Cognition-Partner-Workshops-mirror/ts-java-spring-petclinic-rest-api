package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyReferenceDto;
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
@Transactional(readOnly = true)
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    public List<VetResponseDto> findAll() {
        return vetRepository.findAllWithSpecialties().stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public List<VetResponseDto> findByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetRepository.findBySpecialtyNameContainingIgnoreCase(specialtyName).stream()
            .map(vetMapper::toResponseDto)
            .toList();
    }

    @Transactional
    public VetResponseDto create(VetRequestDto request) {
        Set<Specialty> specialties = resolveSpecialties(request.specialties());
        Vet vet = new Vet(request.firstName(), request.lastName());
        vet.setSpecialties(specialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Transactional
    public VetResponseDto update(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));

        Set<Specialty> specialties = resolveSpecialties(request.specialties());
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(specialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Transactional
    public VetResponseDto delete(Integer id) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
        return vetMapper.toResponseDto(vet);
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyReferenceDto> refs) {
        Set<Specialty> specialties = new HashSet<>();
        if (refs == null || refs.isEmpty()) {
            return specialties;
        }
        for (SpecialtyReferenceDto ref : refs) {
            Specialty specialty = specialtyRepository.findById(ref.id())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", ref.id()));
            specialties.add(specialty);
        }
        return specialties;
    }
}
