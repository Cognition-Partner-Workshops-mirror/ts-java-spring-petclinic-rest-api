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
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    /**
     * Converts a VetRequestDto to a Vet entity.
     * Specialties are resolved separately in the service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    /**
     * Converts a Vet entity to a VetResponseDto.
     */
    VetResponseDto toResponseDto(Vet entity);

    /**
     * Converts a collection of Vet entities to a list of VetResponseDtos.
     */
    List<VetResponseDto> toResponseDtos(Collection<Vet> entities);
}
