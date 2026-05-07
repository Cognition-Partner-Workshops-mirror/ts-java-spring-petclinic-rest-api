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
    public List<VetResponse> findAll() {
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponse)
            .toList();
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
        vet.setSpecialties(resolveSpecialties(request.specialtyIds()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    public VetResponse update(Integer id, VetRequest request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.setSpecialties(resolveSpecialties(request.specialtyIds()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    public VetResponse delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
        return vetMapper.toResponse(vet);
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findBySpecialty(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<VetResponse> searchByName(String name) {
        return vetRepository.searchByName(name).stream()
            .map(vetMapper::toResponse)
            .toList();
    }

    private Set<Specialty> resolveSpecialties(List<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Specialty> found = specialtyRepository.findAllById(specialtyIds);
        if (found.size() != specialtyIds.size()) {
            Set<Integer> foundIds = new HashSet<>();
            for (Specialty s : found) {
                foundIds.add(s.getId());
            }
            for (Integer requestedId : specialtyIds) {
                if (!foundIds.contains(requestedId)) {
                    throw new ResourceNotFoundException("Specialty", requestedId);
                }
            }
        }
        return new HashSet<>(found);
    }
}
