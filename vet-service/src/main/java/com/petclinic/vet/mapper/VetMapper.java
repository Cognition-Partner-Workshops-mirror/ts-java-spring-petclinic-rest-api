package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Specialties are sorted alphabetically by name in the response, matching the monolith behaviour.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSortedSpecialties(vet.getSpecialties()))")
    VetDto toDto(Vet vet);

    List<VetDto> toDtoList(Collection<Vet> vets);

    /**
     * Converts the specialty set to a sorted list of DTOs (alphabetical by name).
     */
    default List<SpecialtyDto> mapSortedSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
            .map(s -> new SpecialtyDto(s.getId(), s.getName()))
            .sorted(Comparator.comparing(SpecialtyDto::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }
}
