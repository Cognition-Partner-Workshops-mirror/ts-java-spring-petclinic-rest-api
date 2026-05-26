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
 * MapStruct mapper for converting between Vet entities and DTOs.
 * Uses {@link SpecialtyMapper} for nested specialty conversion.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    /**
     * Converts a Vet entity (with its specialty set) to a response DTO.
     * The specialties set is mapped to a sorted list for consistent ordering.
     */
    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    /**
     * Helper that converts the Set of Specialty entities to a sorted List of DTOs.
     * Sorting by name provides deterministic output for API consumers.
     */
    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> {
                // Sort by name for deterministic ordering
                if (a.getName() == null && b.getName() == null) return 0;
                if (a.getName() == null) return -1;
                if (b.getName() == null) return 1;
                return a.getName().compareTo(b.getName());
            })
            .collect(Collectors.toList());
    }
}
