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
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        return vetMapper.toResponseDto(vet);
    }

    @Override
    public VetResponseDto createVet(VetRequestDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        Set<Specialty> specialties = resolveSpecialties(dto.getSpecialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto updateVet(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        vetMapper.updateEntity(dto, vet);
        Set<Specialty> specialties = resolveSpecialties(dto.getSpecialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    @Override
    public VetResponseDto deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + id));
        vetRepository.delete(vet);
        return vetMapper.toResponseDto(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtoList(vetRepository.searchByName(name));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponseDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos == null) {
            return specialties;
        }
        for (SpecialtyResponseDto dto : specialtyDtos) {
            if (dto.getId() != null) {
                Specialty specialty = specialtyRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Specialty not found with id: " + dto.getId()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
