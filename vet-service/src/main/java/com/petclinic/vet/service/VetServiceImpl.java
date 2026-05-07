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
        return vetMapper.toResponseDtoList(vetRepository.findAll());
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
        Vet vet = new Vet();
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.firstName());
        existing.setLastName(dto.lastName());
        existing.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto delete(Integer id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(existing);
        vetRepository.delete(existing);
        return response;
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

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findByLastNameAndSpecialtyName(String lastName, String specialtyName) {
        return vetMapper.toResponseDtoList(
            vetRepository.findByLastNameAndSpecialtyName(lastName, specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (Integer specialtyId : specialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
            specialties.add(specialty);
        }
        return specialties;
    }
}
