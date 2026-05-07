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
import java.util.stream.Collectors;

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
        return vetMapper.toResponseDtoList(vetRepository.findAllWithSpecialties());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto findById(Integer id) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto dto) {
        Vet vet = new Vet(dto.firstName(), dto.lastName());
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        vet.setSpecialties(specialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        vet.setSpecialties(specialties);
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto delete(Integer id) {
        Vet vet = vetRepository.findByIdWithSpecialties(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(Integer specialtyId) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByLastName(String lastName) {
        return vetMapper.toResponseDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        if (specialtyDtos == null || specialtyDtos.isEmpty()) {
            return new HashSet<>();
        }
        List<Integer> ids = specialtyDtos.stream()
            .map(SpecialtyResponseDto::id)
            .collect(Collectors.toList());
        List<Specialty> found = specialtyRepository.findByIdIn(ids);
        if (found.size() != ids.size()) {
            List<Integer> foundIds = found.stream().map(Specialty::getId).toList();
            List<Integer> missing = ids.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ResourceNotFoundException("Specialty", missing.isEmpty() ? 0 : missing.get(0));
        }
        return new HashSet<>(found);
    }
}
