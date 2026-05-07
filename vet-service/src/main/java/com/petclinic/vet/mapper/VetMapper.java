package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    VetResponseDto toResponseDto(Vet entity);

    List<VetResponseDto> toResponseDtoList(List<Vet> entities);
}
