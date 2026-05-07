package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;

    public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vet findById(int id) {
        return vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
    }

    @Override
    public Vet create(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        return vetRepository.save(vet);
    }

    @Override
    public Vet update(int id, VetRequestDto dto) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSpecialties(resolveSpecialties(dto.getSpecialtyIds()));
        return vetRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> findBySpecialtyName(String name) {
        return vetRepository.findBySpecialtyName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vet> findByLastName(String lastName) {
        return vetRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialty> specialties = new HashSet<>();
        for (Integer specialtyId : specialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", specialtyId));
            specialties.add(specialty);
        }
        return specialties;
    }
}
