package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Uses SpecialtyMapper for nested specialty conversions.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    /** Convert entity to response DTO, sorting specialties by name. */
    VetResponseDto toResponseDto(Vet vet);

    /** Convert a collection of entities to response DTOs. */
    List<VetResponseDto> toResponseDtos(Collection<Vet> vets);

    /** Convert request DTO to a new entity (id and audit fields are auto-managed). */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);
}
