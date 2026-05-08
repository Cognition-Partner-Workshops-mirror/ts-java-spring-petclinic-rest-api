package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /** Convert entity to response DTO. */
    SpecialtyResponseDto toResponseDto(Specialty specialty);

    /** Convert a collection of entities to response DTOs. */
    List<SpecialtyResponseDto> toResponseDtos(Collection<Specialty> specialties);

    /** Convert request DTO to a new entity (id is auto-generated). */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    /** Update an existing entity from a request DTO. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
