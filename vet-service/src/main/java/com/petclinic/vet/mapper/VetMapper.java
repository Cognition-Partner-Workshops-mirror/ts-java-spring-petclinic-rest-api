package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Handles the specialties set-to-list conversion via a custom default method.
 */
@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface VetMapper {

    /** Convert entity to response DTO, mapping the specialties set to a sorted list */
    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    /** Convert a set of Specialty entities to a sorted list of response DTOs */
    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
            .collect(Collectors.toList());
    }
}
