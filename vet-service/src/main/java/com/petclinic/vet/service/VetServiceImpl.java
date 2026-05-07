package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.LinkedHashSet;
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
    public List<VetDto> listAll() {
        return vetMapper.toDtoList(vetRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto getById(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetRequestDto dto) {
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        Vet vet = vetMapper.toEntity(dto, specialties);
        return vetMapper.toDto(vetRepository.save(vet));
    }

    @Override
    public VetDto update(int id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        Set<Specialty> specialties = resolveSpecialties(dto.specialties());
        vetMapper.updateEntity(dto, vet, specialties);
        return vetMapper.toDto(vetRepository.save(vet));
    }

    @Override
    public VetDto delete(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetDto dto = vetMapper.toDto(vet);
        vetRepository.delete(vet);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialty(String specialtyName) {
        return vetMapper.toDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findByLastName(String lastName) {
        return vetMapper.toDtoList(vetRepository.findByLastNameContainingIgnoreCase(lastName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyAndLastName(String specialtyName, String lastName) {
        return vetMapper.toDtoList(
            vetRepository.findBySpecialtyNameAndLastNameContainingIgnoreCase(specialtyName, lastName));
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        Set<Specialty> specialties = new LinkedHashSet<>();
        if (specialtyDtos != null) {
            for (SpecialtyDto dto : specialtyDtos) {
                if (dto.id() != null) {
                    Specialty s = specialtyRepository.findById(dto.id())
                        .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                    specialties.add(s);
                }
            }
        }
        return specialties;
    }
}
