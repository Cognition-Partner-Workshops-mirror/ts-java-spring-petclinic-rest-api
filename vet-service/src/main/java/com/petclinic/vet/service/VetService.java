package com.petclinic.vet.service;

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
import java.util.stream.Collectors;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional(readOnly = true)
    public List<Vet> findAllVets() {
        return vetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vet findVetById(Integer id) {
        return vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
    }

    public Vet saveVet(Vet vet) {
        resolveSpecialties(vet);
        return vetRepository.save(vet);
    }

    public Vet updateVet(Integer id, Vet updated) {
        Vet existing = findVetById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialties(new HashSet<>());
        existing.getSpecialties().addAll(updated.getSpecialties());
        resolveSpecialties(existing);
        return vetRepository.save(existing);
    }

    public void deleteVet(Integer id) {
        Vet vet = findVetById(id);
        vetRepository.delete(vet);
    }

    @Transactional(readOnly = true)
    public List<Vet> searchVetsByName(String name) {
        return vetRepository.searchByName(name);
    }

    @Transactional(readOnly = true)
    public List<Vet> findVetsBySpecialty(String specialtyName) {
        return vetRepository.findBySpecialtyName(specialtyName);
    }

    @Transactional(readOnly = true)
    public List<Specialty> findAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Specialty findSpecialtyById(Integer id) {
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
    }

    public Specialty saveSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public Specialty updateSpecialty(Integer id, Specialty updated) {
        Specialty existing = findSpecialtyById(id);
        existing.setName(updated.getName());
        return specialtyRepository.save(existing);
    }

    public void deleteSpecialty(Integer id) {
        Specialty specialty = findSpecialtyById(id);
        specialtyRepository.delete(specialty);
    }

    private void resolveSpecialties(Vet vet) {
        if (vet.getSpecialties() == null || vet.getSpecialties().isEmpty()) {
            return;
        }
        Set<String> names = vet.getSpecialties().stream()
            .map(Specialty::getName)
            .collect(Collectors.toSet());
        List<Specialty> resolved = specialtyRepository.findByNameInIgnoreCase(names);
        vet.setSpecialties(new HashSet<>(resolved));
    }
}
