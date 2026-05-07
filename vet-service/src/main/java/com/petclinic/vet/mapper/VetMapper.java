package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(specialtiesToResponseDtoList(entity.getSpecialties()))")
    VetResponseDto toResponseDto(Vet entity);

    List<VetResponseDto> toResponseDtoList(List<Vet> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> specialtiesToResponseDtoList(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<SpecialtyResponseDto> list = new ArrayList<>();
        for (Specialty s : specialties) {
            list.add(new SpecialtyResponseDto(s.getId(), s.getName()));
        }
        return list;
    }
}
