package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
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
    public List<VetDto> findAll() {
        return vetMapper.toDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetDto dto) {
        Vet vet = vetMapper.toEntity(dto);
        vet.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto update(Integer id, VetDto dto) {
        Vet existing = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSpecialties(resolveSpecialties(dto.getSpecialties()));
        Vet saved = vetRepository.save(existing);
        return vetMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Vet existing = vetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyId(Integer specialtyId) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyId(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findByLastName(String lastName) {
        return vetMapper.toDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos != null) {
            for (SpecialtyDto dto : specialtyDtos) {
                if (dto.getId() != null) {
                    Specialty specialty = specialtyRepository.findById(dto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getId()));
                    specialties.add(specialty);
                }
            }
        }
        return specialties;
    }
}
