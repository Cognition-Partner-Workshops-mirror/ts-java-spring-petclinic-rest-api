package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponse;
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
        return vetMapper.toResponseList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponse getById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    @Override
    public VetResponse create(VetRequest request) {
        Vet vet = new Vet();
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponse update(Integer id, VetRequest request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponse delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponse response = vetMapper.toResponse(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> searchByLastName(String lastName) {
        return vetMapper.toResponseList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> filterBySpecialty(String specialtyName) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponse> searchByLastNameAndSpecialty(String lastName, String specialtyName) {
        return vetMapper.toResponseList(
            vetRepository.findByLastNameContainingIgnoreCaseAndSpecialtyName(lastName, specialtyName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyResponse> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos == null) {
            return specialties;
        }
        for (SpecialtyResponse dto : specialtyDtos) {
            Specialty specialty = specialtyRepository.findById(dto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
            specialties.add(specialty);
        }
        return specialties;
    }
}
