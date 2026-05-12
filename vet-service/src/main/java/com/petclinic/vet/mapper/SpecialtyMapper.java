package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 * Uses Spring component model for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    // Convert entity to response DTO
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    // Convert list of entities to list of response DTOs
    List<SpecialtyResponseDto> toResponseDtoList(List<Specialty> specialties);

    // Convert request DTO to entity (id, audit fields are auto-generated)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    // Update existing entity fields from request DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
