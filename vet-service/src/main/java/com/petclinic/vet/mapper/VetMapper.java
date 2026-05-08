package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Handles the specialties collection mapping.
 */
@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface VetMapper {

    /**
     * Convert Vet entity to response DTO with specialties as a list.
     */
    @Mapping(target = "specialties", expression = "java(specialtiesToDtoList(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    /**
     * Convert request DTO to entity (specialties handled separately in service).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    /**
     * Update existing vet entity from request DTO (specialties handled separately).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(VetRequestDto dto, @MappingTarget Vet vet);

    /**
     * Convert a set of Specialty entities to a list of response DTOs.
     */
    default List<SpecialtyResponseDto> specialtiesToDtoList(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .collect(Collectors.toList());
    }
}
