package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    private final SpecialtyMapper specialtyMapper;

    public VetMapper(SpecialtyMapper specialtyMapper) {
        this.specialtyMapper = specialtyMapper;
    }

    public VetResponseDto toResponseDto(Vet entity) {
        List<SpecialtyResponseDto> specialties = entity.getSpecialties().stream()
            .sorted(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER))
            .map(specialtyMapper::toResponseDto)
            .collect(Collectors.toList());
        return new VetResponseDto(entity.getVetId(), entity.getFirstName(), entity.getLastName(), specialties);
    }

    public Vet toEntity(VetRequestDto dto, Set<Specialty> specialties) {
        Vet entity = new Vet();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setSpecialties(specialties);
        return entity;
    }

    public void updateEntity(Vet entity, VetRequestDto dto, Set<Specialty> specialties) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setSpecialties(specialties);
    }
}
