package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponseDto toResponseDto(Specialty specialty);

    List<SpecialtyResponseDto> toResponseDtos(Collection<Specialty> specialties);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(SpecialtyRequestDto dto);

    List<Specialty> toEntities(Collection<SpecialtyRequestDto> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
