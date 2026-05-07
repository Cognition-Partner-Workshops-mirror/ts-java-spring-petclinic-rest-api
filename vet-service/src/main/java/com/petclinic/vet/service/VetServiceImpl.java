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
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
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
        Vet vet = new Vet();
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto request) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto deleteVet(Integer id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(existing);
        return vetMapper.toResponseDto(existing);
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

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequestDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyRequestDto dto : dtos) {
            List<Specialty> found = specialtyRepository.findByNameContainingIgnoreCase(dto.name());
            Specialty match = found.stream()
                .filter(s -> s.getName().equalsIgnoreCase(dto.name()))
                .findFirst()
                .orElseGet(() -> {
                    Specialty newSpec = new Specialty();
                    newSpec.setName(dto.name());
                    return specialtyRepository.save(newSpec);
                });
            specialties.add(match);
        }
        return specialties;
    }
}
