package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

/**
 * MapStruct mapper for Vet entity <-> DTO conversions.
 * Specialties are ignored here because VetServiceImpl resolves them from the DB.
 */
@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtos(List<Vet> vets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    void updateEntity(VetRequestDto dto, @MappingTarget Vet vet);
}
