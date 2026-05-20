package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for converting between Specialty entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /** Convert request DTO to entity, ignoring auto-generated fields. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    /** Convert entity to response DTO. */
    SpecialtyResponseDto toResponseDto(Specialty entity);

    /** Convert a list of entities to a list of response DTOs. */
    List<SpecialtyResponseDto> toResponseDtos(List<Specialty> entities);

    /** Convert a set of entities to a list of response DTOs. */
    List<SpecialtyResponseDto> toResponseDtos(Set<Specialty> entities);

    /** Update existing entity from request DTO, ignoring auto-generated fields. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
