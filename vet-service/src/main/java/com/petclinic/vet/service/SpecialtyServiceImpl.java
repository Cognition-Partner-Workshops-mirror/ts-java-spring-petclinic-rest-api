package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Transactional implementation of SpecialtyService for CRUD operations on specialties. */
@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository repository;
    private final SpecialtyMapper mapper;

    public SpecialtyServiceImpl(SpecialtyRepository repository, SpecialtyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto findById(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toDto(specialty);
    }

    @Override
    public SpecialtyDto create(SpecialtyDto dto) {
        Specialty entity = mapper.toEntity(dto);
        entity.setId(null);
        Specialty saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public SpecialtyDto update(Integer id, SpecialtyDto dto) {
        Specialty existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(dto, existing);
        Specialty saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public SpecialtyDto delete(Integer id) {
        Specialty existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyDto dto = mapper.toDto(existing);
        repository.delete(existing);
        return dto;
    }
}
