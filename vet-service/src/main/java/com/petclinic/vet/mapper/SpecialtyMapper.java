package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper to convert between Specialty entity and SpecialtyDto.
 * Ignores audit and relationship fields that are managed by JPA lifecycle callbacks.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyDto toDto(Specialty entity);

    /** Map DTO to entity, ignoring JPA-managed audit fields and inverse relationship. */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "vets", ignore = true)
    Specialty toEntity(SpecialtyDto dto);

    List<SpecialtyDto> toDtoList(List<Specialty> entities);

    /** Update existing entity from DTO, ignoring audit and relationship fields. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "vets", ignore = true)
    void updateEntity(SpecialtyDto dto, @MappingTarget Specialty entity);
}
