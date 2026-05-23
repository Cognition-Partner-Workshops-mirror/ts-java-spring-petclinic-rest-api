package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

/**
 * MapStruct mapper for converting between Specialty entity and SpecialtyDto.
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    /**
     * Converts a SpecialtyDto to a Specialty entity.
     * The id field is ignored since it is auto-generated.
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyDto dto);

    /**
     * Converts a Specialty entity to a SpecialtyDto.
     */
    SpecialtyDto toDto(Specialty entity);

    /**
     * Converts a collection of Specialty entities to a list of SpecialtyDtos.
     */
    List<SpecialtyDto> toDtos(Collection<Specialty> entities);
}
