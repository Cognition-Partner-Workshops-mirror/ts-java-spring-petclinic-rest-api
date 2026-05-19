package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 * Uses Spring component model for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /** Convert entity to response DTO */
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    /** Convert request DTO to a new entity */
    Specialty toEntity(SpecialtyRequestDto dto);

    /** Update an existing entity from a request DTO */
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty specialty);
}
