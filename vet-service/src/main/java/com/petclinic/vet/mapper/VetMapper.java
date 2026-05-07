package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    private final SpecialtyMapper specialtyMapper;

    public VetMapper(SpecialtyMapper specialtyMapper) {
        this.specialtyMapper = specialtyMapper;
    }

    public VetDto toDto(Vet entity) {
        List<SpecialtyDto> specialties = entity.getSpecialties().stream()
            .map(specialtyMapper::toDto)
            .toList();
        return new VetDto(entity.getId(), entity.getFirstName(), entity.getLastName(), specialties);
    }

    public List<VetDto> toDtoList(List<Vet> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Vet toEntity(VetRequestDto dto, Set<Specialty> specialties) {
        Vet vet = new Vet();
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(specialties);
        return vet;
    }

    public void updateEntity(VetRequestDto dto, Vet vet, Set<Specialty> specialties) {
        vet.setFirstName(dto.firstName());
        vet.setLastName(dto.lastName());
        vet.setSpecialties(specialties);
    }
}
