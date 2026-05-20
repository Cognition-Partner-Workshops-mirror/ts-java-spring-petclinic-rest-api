package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Uses SpecialtyMapper for nested specialty conversions.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    /** Convert entity to response DTO, using sorted specialties for consistent ordering. */
    @Mapping(source = "sortedSpecialties", target = "specialties")
    VetResponseDto toResponseDto(Vet entity);

    /** Convert a list of entities to a list of response DTOs. */
    List<VetResponseDto> toResponseDtos(List<Vet> entities);

    /** Convert request DTO to entity, ignoring auto-generated and relationship fields. */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);
}
