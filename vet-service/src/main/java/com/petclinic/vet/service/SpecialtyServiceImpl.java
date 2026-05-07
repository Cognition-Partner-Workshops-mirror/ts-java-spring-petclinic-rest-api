package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public List<SpecialtyResponseDto> findAll() {
        return specialtyMapper.toResponseDtos(specialtyRepository.findAll());
    }

    @Override
    public SpecialtyResponseDto findById(int id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponseDto update(int id, SpecialtyRequestDto dto) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyMapper.updateEntity(dto, specialty);
        specialty = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponseDto delete(int id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(specialty);
        specialtyRepository.delete(specialty);
        return response;
    }
}
