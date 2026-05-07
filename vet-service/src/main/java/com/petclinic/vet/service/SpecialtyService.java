package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
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

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyService(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Transactional(readOnly = true)
    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Specialty findById(int id) {
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
    }

    public Specialty create(SpecialtyRequestDto dto) {
        Specialty specialty = specialtyMapper.toEntity(dto);
        return specialtyRepository.save(specialty);
    }

    public Specialty update(int id, SpecialtyRequestDto dto) {
        Specialty existing = findById(id);
        specialtyMapper.updateEntity(dto, existing);
        return specialtyRepository.save(existing);
    }

    public void delete(int id) {
        Specialty existing = findById(id);
        specialtyRepository.delete(existing);
    }
}
