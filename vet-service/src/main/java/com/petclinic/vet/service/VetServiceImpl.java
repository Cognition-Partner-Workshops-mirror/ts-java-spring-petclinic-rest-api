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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    public List<VetResponse> findAll() {
        return vetMapper.toResponseList(vetRepository.findAll());
    }

    @Override
    public VetResponse findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    @Override
    @Transactional
    public VetResponse create(VetRequest request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponse(vet);
    }

    @Override
    @Transactional
    public VetResponse update(Integer id, VetRequest request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        vet = vetRepository.save(vet);
        return vetMapper.toResponse(vet);
    }

    @Override
    @Transactional
    public VetResponse delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponse response = vetMapper.toResponse(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    public List<VetResponse> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    public List<VetResponse> searchByName(String name) {
        return vetMapper.toResponseList(vetRepository.searchByName(name));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequest> specialtyRequests) {
        if (specialtyRequests == null || specialtyRequests.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> names = specialtyRequests.stream()
            .map(SpecialtyRequest::name)
            .collect(Collectors.toSet());
        List<Specialty> found = specialtyRepository.findByNameIn(names);
        return new HashSet<>(found);
    }
}
