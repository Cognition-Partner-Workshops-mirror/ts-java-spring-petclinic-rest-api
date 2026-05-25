package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Delegates specialty mapping to {@link SpecialtyMapper}.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    /** Convert entity to response DTO, mapping nested specialties automatically */
    VetResponseDto toResponseDto(Vet vet);

    /** Convert list of entities to list of response DTOs */
    List<VetResponseDto> toResponseDtoList(List<Vet> vets);
}
