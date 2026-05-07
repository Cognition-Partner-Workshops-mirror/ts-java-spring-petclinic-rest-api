package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(specialtiesToDtoList(entity.getSpecialties()))")
    VetResponseDto toResponseDto(Vet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> specialtiesToDtoList(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .collect(Collectors.toList());
    }
}
