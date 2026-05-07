package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper to convert between Vet entity and VetDto.
 * Converts the Set<Specialty> to a sorted List<SpecialtyDto> for deterministic JSON output.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", source = "specialties", qualifiedByName = "specialtiesToDtoList")
    VetDto toDto(Vet entity);

    List<VetDto> toDtoList(List<Vet> entities);

    /** Convert Set<Specialty> to a sorted List<SpecialtyDto> ordered by ID. */
    @Named("specialtiesToDtoList")
    default List<SpecialtyDto> specialtiesToDtoList(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
            .map(s -> new SpecialtyDto(s.getId(), s.getName()))
            .sorted((a, b) -> {
                if (a.id() == null || b.id() == null) return 0;
                return a.id().compareTo(b.id());
            })
            .toList();
    }
}
