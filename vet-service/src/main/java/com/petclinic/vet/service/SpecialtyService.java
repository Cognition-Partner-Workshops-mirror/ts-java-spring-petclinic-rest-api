package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.exception.DuplicateResourceException;
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
        SpecialtyEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        if (repository.existsByNameIgnoreCase(dto.name())) {
            throw new DuplicateResourceException("Specialty already exists with name: " + dto.name());
        }
        SpecialtyEntity entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto) {
        SpecialtyEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        repository.findByNameIgnoreCase(dto.name())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new DuplicateResourceException("Specialty already exists with name: " + dto.name());
            });

        mapper.updateEntity(dto, entity);
        entity = repository.save(entity);
        return mapper.toResponseDto(entity);
    }

    public SpecialtyResponseDto delete(Integer id) {
        SpecialtyEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = mapper.toResponseDto(entity);
        repository.delete(entity);
        return response;
    }
}
