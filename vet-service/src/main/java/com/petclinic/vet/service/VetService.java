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
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository,
                      SpecialtyRepository specialtyRepository,
                      VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto addVet(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto updateVet(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialtyIds()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findByLastName(String lastName) {
        return vetMapper.toResponseDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialtyName(String specialtyName) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Specialty> found = specialtyRepository.findAllById(specialtyIds);
        if (found.size() != specialtyIds.size()) {
            Set<Integer> foundIds = new HashSet<>();
            for (Specialty s : found) {
                foundIds.add(s.getId());
            }
            List<Integer> missing = specialtyIds.stream()
                .filter(sid -> !foundIds.contains(sid))
                .toList();
            throw new ResourceNotFoundException("Specialty", missing);
        }
        return new HashSet<>(found);
    }
}
