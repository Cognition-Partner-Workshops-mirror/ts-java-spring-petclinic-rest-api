package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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
    public List<VetResponseDto> listAll() {
        return vetMapper.toResponseList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetResponseDto getById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponse(vet);
    }

    @Override
    public VetResponseDto create(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponseDto update(Integer id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(resolveSpecialties(dto.specialties()));
        return vetMapper.toResponse(vetRepository.save(vet));
    }

    @Override
    public VetResponseDto delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponse(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(Integer specialtyId) {
        return vetMapper.toResponseList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseList(vetRepository.findByNameContainingIgnoreCase(name));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyRequestDto> dtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (dtos == null) {
            return specialties;
        }
        for (SpecialtyRequestDto dto : dtos) {
            List<Specialty> found = specialtyRepository.findByNameContainingIgnoreCase(dto.name());
            Specialty match = found.stream()
                .filter(s -> s.getName().equalsIgnoreCase(dto.name()))
                .findFirst()
                .orElseGet(() -> {
                    Specialty newSpec = new Specialty();
                    newSpec.setName(dto.name());
                    return specialtyRepository.save(newSpec);
                });
            specialties.add(match);
        }
        return specialties;
    }
}
