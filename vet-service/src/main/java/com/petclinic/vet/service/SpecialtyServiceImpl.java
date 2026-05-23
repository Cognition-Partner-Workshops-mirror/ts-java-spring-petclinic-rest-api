package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of SpecialtyService providing full CRUD for specialties.
 */
@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Specialty findById(int id) {
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
    }

    @Override
    public Specialty save(SpecialtyDto dto) {
        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());
        return specialtyRepository.save(specialty);
    }

    @Override
    public Specialty update(int id, SpecialtyDto dto) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        existing.setName(dto.getName());
        return specialtyRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        Specialty existing = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        specialtyRepository.delete(existing);
    }
}
