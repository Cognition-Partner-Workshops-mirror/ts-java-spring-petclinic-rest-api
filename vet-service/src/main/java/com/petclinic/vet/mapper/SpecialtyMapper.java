package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponseDto toResponseDto(Specialty entity);

    List<SpecialtyResponseDto> toResponseDtoList(List<Specialty> entities);

    Specialty toEntity(SpecialtyRequestDto dto);

    void updateEntity(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
