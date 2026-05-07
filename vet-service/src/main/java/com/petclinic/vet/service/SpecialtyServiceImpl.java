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
    public List<SpecialtyResponseDto> findAll() {
        return specialtyMapper.toResponseDtos(specialtyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto findById(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyMapper.updateEntity(dto, existing);
        Specialty saved = specialtyRepository.save(existing);
        return specialtyMapper.toResponseDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyRepository.delete(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return specialtyMapper.toResponseDtos(specialtyRepository.findByNameContainingIgnoreCase(name));
    }
}
