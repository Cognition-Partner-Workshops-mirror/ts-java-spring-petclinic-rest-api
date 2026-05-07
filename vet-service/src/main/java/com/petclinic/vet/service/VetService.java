package com.petclinic.vet.service;

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
    public List<VetResponse> listAll() {
        return vetMapper.toResponseList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponse getById(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(entity);
    }

    public VetResponse create(VetRequest request) {
        Vet entity = vetMapper.toEntity(request);
        Set<Specialty> specialties = resolveSpecialties(request);
        entity.setSpecialties(specialties);
        return vetMapper.toResponse(vetRepository.save(entity));
    }

    public VetResponse update(Integer id, VetRequest request) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        Set<Specialty> specialties = resolveSpecialties(request);
        entity.setSpecialties(specialties);
        return vetMapper.toResponse(vetRepository.save(entity));
    }

    public VetResponse delete(Integer id) {
        Vet entity = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponse response = vetMapper.toResponse(entity);
        vetRepository.delete(entity);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findBySpecialty(Integer specialtyId) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findByLastName(String lastName) {
        return vetMapper.toResponseList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    private Set<Specialty> resolveSpecialties(VetRequest request) {
        if (request.specialties() == null || request.specialties().isEmpty()) {
            return new HashSet<>();
        }
        List<Integer> ids = request.specialties().stream()
            .map(s -> s.id())
            .toList();
        List<Specialty> found = specialtyRepository.findAllByIdIn(ids);
        if (found.size() != ids.size()) {
            List<Integer> foundIds = found.stream().map(Specialty::getId).toList();
            List<Integer> missing = ids.stream().filter(i -> !foundIds.contains(i)).toList();
            throw new ResourceNotFoundException("Specialty", missing.getFirst());
        }
        return new HashSet<>(found);
    }
}
