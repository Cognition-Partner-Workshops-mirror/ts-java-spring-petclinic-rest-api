package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final VetRepository vetRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyService(SpecialtyRepository specialtyRepository, VetRepository vetRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.vetRepository = vetRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> listSpecialties() {
        return specialtyMapper.toResponseDtoList(specialtyRepository.findAll());
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    public SpecialtyResponseDto addSpecialty(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto dto) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyMapper.updateEntity(dto, specialty);
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        List<Vet> vets = vetRepository.findBySpecialtyId(id);
        for (Vet vet : vets) {
            vet.getSpecialties().remove(specialty);
        }
        vetRepository.saveAll(vets);
        specialtyRepository.delete(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return specialtyMapper.toResponseDtoList(specialtyRepository.findByNameContainingIgnoreCase(name));
    }
}
