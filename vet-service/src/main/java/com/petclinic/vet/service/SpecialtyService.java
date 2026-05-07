package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
    public List<SpecialtyResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public SpecialtyResponse findById(int id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toResponse(entity);
    }

    public SpecialtyResponse create(SpecialtyRequest request) {
        Specialty entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public SpecialtyResponse update(int id, SpecialtyRequest request) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    public SpecialtyResponse delete(int id) {
        Specialty entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        SpecialtyResponse response = mapper.toResponse(entity);
        repository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> searchByName(String name) {
        return mapper.toResponseList(repository.findByNameContainingIgnoreCase(name));
    }
}
