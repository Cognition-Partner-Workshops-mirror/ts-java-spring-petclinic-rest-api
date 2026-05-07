package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository repository;
    private final SpecialtyMapper mapper;

    public SpecialtyServiceImpl(SpecialtyRepository repository, SpecialtyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<SpecialtyResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public SpecialtyResponse findById(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponse(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponse create(SpecialtyRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DuplicateResourceException("Specialty with name '%s' already exists".formatted(request.name()));
        }
        Specialty specialty = mapper.toEntity(request);
        specialty = repository.save(specialty);
        return mapper.toResponse(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponse update(Integer id, SpecialtyRequest request) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(request, specialty);
        specialty = repository.save(specialty);
        return mapper.toResponse(specialty);
    }

    @Override
    @Transactional
    public SpecialtyResponse delete(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponse response = mapper.toResponse(specialty);
        repository.delete(specialty);
        return response;
    }
}
