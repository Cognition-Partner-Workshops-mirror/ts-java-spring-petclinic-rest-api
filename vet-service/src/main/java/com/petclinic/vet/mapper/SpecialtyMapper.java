package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 * Uses Spring component model for injection into service classes.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /** Convert entity to response DTO */
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    /** Convert a list of entities to response DTOs */
    List<SpecialtyResponseDto> toResponseDtoList(List<Specialty> specialties);

    /** Convert request DTO to a new entity */
    Specialty toEntity(SpecialtyRequestDto dto);

    /** Update an existing entity from a request DTO (preserves id and audit fields) */
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty specialty);
}
