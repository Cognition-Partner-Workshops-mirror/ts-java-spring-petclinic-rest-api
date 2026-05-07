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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository,
                          SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> listAll() {
        return vetMapper.toResponseList(vetRepository.findAllWithSpecialties());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponse getById(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    @Override
    public VetResponse create(VetRequest request) {
        Vet vet = vetMapper.toEntity(request);
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponse update(int id, VetRequest request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponse delete(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponse response = vetMapper.toResponse(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> findByLastName(String lastName) {
        return vetMapper.toResponseList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyName(specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequest> requests) {
        Set<Specialty> specialties = new HashSet<>();
        for (SpecialtyRequest sr : requests) {
            Specialty specialty = specialtyRepository.findByNameIgnoreCase(sr.name())
                .orElseGet(() -> {
                    Specialty s = new Specialty();
                    s.setName(sr.name());
                    return specialtyRepository.save(s);
                });
            specialties.add(specialty);
        }
        return specialties;
    }
}
