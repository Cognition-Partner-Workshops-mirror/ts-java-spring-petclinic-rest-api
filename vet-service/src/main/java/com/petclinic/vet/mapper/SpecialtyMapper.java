package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /**
     * Convert entity to response DTO.
     */
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    /**
     * Convert request DTO to entity (for creation).
     */
    Specialty toEntity(SpecialtyRequestDto dto);

    /**
     * Update existing entity fields from request DTO.
     */
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty specialty);
}
