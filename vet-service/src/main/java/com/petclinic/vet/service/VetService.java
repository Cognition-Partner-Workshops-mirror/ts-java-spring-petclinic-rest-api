package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findAll() {
        return vetMapper.toResponseList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponse findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    public VetResponse create(VetRequest request) {
        Vet vet = new Vet();
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponse(saved);
    }

    public VetResponse update(Integer id, VetRequest request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponse(saved);
    }

    public VetResponse delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
        return vetMapper.toResponse(vet);
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Transactional(readOnly = true)
    public List<VetResponse> searchByLastName(String lastName) {
        return vetMapper.toResponseList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequest> specialtyRequests) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyRequests == null) {
            return specialties;
        }
        for (SpecialtyRequest sr : specialtyRequests) {
            List<Specialty> found = specialtyRepository.findByNameContainingIgnoreCase(sr.name());
            if (!found.isEmpty()) {
                specialties.add(found.get(0));
            } else {
                Specialty newSpecialty = new Specialty();
                newSpecialty.setName(sr.name());
                specialties.add(specialtyRepository.save(newSpecialty));
            }
        }
        return specialties;
    }
}
