package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between Specialty entities and DTOs.
 * Registered as a Spring component via componentModel = "spring".
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /** Converts a Specialty entity to its response DTO. */
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    /** Creates a new Specialty entity from a request DTO (auto-generated fields are ignored). */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "vets", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    /** Updates an existing Specialty entity in-place from a request DTO. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "vets", ignore = true)
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
