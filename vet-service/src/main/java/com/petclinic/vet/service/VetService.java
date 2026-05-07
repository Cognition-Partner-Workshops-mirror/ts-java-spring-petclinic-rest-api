package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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
    public List<VetResponseDto> listVets() {
        return vetMapper.toResponseDtoList(vetRepository.findAll());
    }

    @Transactional(readOnly = true)
    public VetResponseDto getVet(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toResponseDto(vet);
    }

    public VetResponseDto createVet(VetRequestDto dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto updateVet(int id, VetRequestDto dto) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setSpecialties(resolveSpecialties(dto));
        Vet saved = vetRepository.save(vet);
        return vetMapper.toResponseDto(saved);
    }

    public VetResponseDto deleteVet(int id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetRepository.delete(vet);
        return response;
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> findBySpecialty(String specialtyName) {
        return vetMapper.toResponseDtoList(vetRepository.findBySpecialtyName(specialtyName));
    }

    @Transactional(readOnly = true)
    public List<VetResponseDto> searchByName(String name) {
        return vetMapper.toResponseDtoList(vetRepository.searchByName(name));
    }

    private Set<Specialty> resolveSpecialties(VetRequestDto dto) {
        Set<Specialty> specialties = new HashSet<>();
        if (dto.getSpecialties() != null) {
            for (var specDto : dto.getSpecialties()) {
                List<Specialty> found = specialtyRepository.findByNameContainingIgnoreCase(specDto.getName());
                if (!found.isEmpty()) {
                    specialties.add(found.getFirst());
                } else {
                    Specialty newSpecialty = new Specialty();
                    newSpecialty.setName(specDto.getName());
                    specialties.add(specialtyRepository.save(newSpecialty));
                }
            }
        }
        return specialties;
    }
}
