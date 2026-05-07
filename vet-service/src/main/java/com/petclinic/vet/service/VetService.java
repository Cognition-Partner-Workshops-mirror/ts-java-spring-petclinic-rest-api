package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.VetRequest;
import com.petclinic.vet.dto.response.VetResponse;
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
    public List<VetResponse> listVets() {
        return vetRepository.findAll().stream()
            .map(vetMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public VetResponse getVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    public VetResponse createVet(VetRequest request) {
        Set<Specialty> specialties = resolveSpecialties(request.getSpecialtyIds());

        Vet vet = new Vet();
        vet.setFirstName(request.getFirstName());
        vet.setLastName(request.getLastName());
        vet.setSpecialties(specialties);

        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponse(saved);
    }

    public VetResponse updateVet(Integer id, VetRequest request) {
        Vet existing = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));

        Set<Specialty> specialties = resolveSpecialties(request.getSpecialtyIds());

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setSpecialties(specialties);

        Vet saved = vetRepository.save(existing);
        return vetMapper.toResponse(saved);
    }

    public VetResponse deleteVet(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponse response = vetMapper.toResponse(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponse> findBySpecialty(Integer specialtyId) {
        if (!specialtyRepository.existsById(specialtyId)) {
            throw new ResourceNotFoundException("Specialty", specialtyId);
        }
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<VetResponse> searchByLastName(String lastName) {
        return vetRepository.searchByLastName(lastName).stream()
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
            List<Integer> missing = specialtyIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
            throw new ResourceNotFoundException("Specialty", missing);
        }
        return new HashSet<>(found);
    }
}
