package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyDto toDto(Specialty entity);

    Specialty toEntity(SpecialtyDto dto);

    List<SpecialtyDto> toDtoList(Collection<Specialty> entities);

    List<Specialty> toEntityList(Collection<SpecialtyDto> dtos);
}
