package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> listSpecialties() {
        return specialtyMapper.toResponseDtoList(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto updateSpecialty(Integer id, SpecialtyRequestDto dto) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        specialtyMapper.updateEntity(dto, specialty);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto deleteSpecialty(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        specialtyRepository.delete(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }
}
