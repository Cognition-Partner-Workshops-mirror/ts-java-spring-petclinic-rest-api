package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<SpecialtyResponseDto> findAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto findById(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponseDto(specialty);
    }

    @Override
    public SpecialtyResponseDto create(SpecialtyRequestDto dto) {
        if (repository.existsByNameIgnoreCase(dto.name())) {
            throw new DuplicateResourceException("Specialty", "name", dto.name());
        }
        Specialty entity = mapper.toEntity(dto);
        Specialty saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto dto) {
        Specialty existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        repository.findByNameIgnoreCase(dto.name())
            .filter(s -> !s.getId().equals(id))
            .ifPresent(s -> {
                throw new DuplicateResourceException("Specialty", "name", dto.name());
            });

        mapper.updateEntity(dto, existing);
        Specialty saved = repository.save(existing);
        return mapper.toResponseDto(saved);
    }

    @Override
    public SpecialtyResponseDto delete(Integer id) {
        Specialty existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = mapper.toResponseDto(existing);
        repository.delete(existing);
        return response;
    }
}
