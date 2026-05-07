package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;
import java.util.Set;

/** MapStruct mapper for Specialty entity <-> DTO conversions. */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyDto toDto(Specialty specialty);

    List<SpecialtyDto> toDtos(List<Specialty> specialties);

    Set<SpecialtyDto> toDtoSet(Set<Specialty> specialties);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SpecialtyRequestDto dto, @MappingTarget Specialty specialty);
}
