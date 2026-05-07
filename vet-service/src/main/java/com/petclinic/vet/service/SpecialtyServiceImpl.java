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

    private final SpecialtyRepository repository;
    private final SpecialtyMapper mapper;

    public SpecialtyServiceImpl(SpecialtyRepository repository, SpecialtyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> listAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getById(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponseDto(entity);
    }

    @Override
    public SpecialtyResponseDto create(SpecialtyRequestDto request) {
        Specialty entity = mapper.toEntity(request);
        entity = repository.save(entity);
        return mapper.toResponseDto(entity);
    }

    @Override
    public SpecialtyResponseDto update(Integer id, SpecialtyRequestDto request) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(request, entity);
        entity = repository.save(entity);
        return mapper.toResponseDto(entity);
    }

    @Override
    public SpecialtyResponseDto delete(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponseDto response = mapper.toResponseDto(entity);
        repository.delete(entity);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> searchByName(String name) {
        return mapper.toResponseDtoList(repository.findByNameContainingIgnoreCase(name));
    }
}
