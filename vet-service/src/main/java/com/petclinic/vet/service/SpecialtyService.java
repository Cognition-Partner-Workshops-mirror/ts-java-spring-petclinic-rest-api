package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpecialtyService {

    private final SpecialtyRepository repository;
    private final SpecialtyMapper mapper;

    public SpecialtyService(SpecialtyRepository repository, SpecialtyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> listAll() {
        return repository.findAll().stream()
            .map(mapper::toResponseDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDto getById(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        Specialty entity = mapper.toEntity(dto);
        Specialty saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(dto, entity);
        Specialty saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    public SpecialtyResponseDto delete(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = mapper.toResponseDto(entity);
        repository.delete(entity);
        return response;
    }
}
