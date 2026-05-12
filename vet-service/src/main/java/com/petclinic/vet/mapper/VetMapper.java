package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for converting Vet entities to response DTOs.
 * Vet creation/update uses manual mapping in the service layer because
 * specialty assignment requires repository lookups by ID.
 */
@Mapper(componentModel = "spring")
public interface VetMapper {

    /** Convert entity to response DTO, mapping the specialty set to a sorted list */
    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    /** Convert a list of entities to response DTOs */
    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    /** Map specialty entity to its response DTO */
    SpecialtyResponseDto toSpecialtyResponseDto(Specialty specialty);

    /** Convert the specialty set to a sorted list of response DTOs */
    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
                .map(this::toSpecialtyResponseDto)
                .sorted((a, b) -> {
                    if (a.getId() == null || b.getId() == null) return 0;
                    return a.getId().compareTo(b.getId());
                })
                .toList();
    }
}
