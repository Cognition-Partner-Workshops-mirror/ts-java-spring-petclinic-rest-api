package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
    public List<SpecialtyResponse> listAll() {
        return repository.findAll().stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public SpecialtyResponse getById(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponse(entity);
    }

    public SpecialtyResponse create(SpecialtyRequest request) {
        Specialty entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public SpecialtyResponse update(Integer id, SpecialtyRequest request) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public SpecialtyResponse delete(Integer id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponse response = mapper.toResponse(entity);
        repository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
            .map(mapper::toResponse)
            .toList();
    }
}
