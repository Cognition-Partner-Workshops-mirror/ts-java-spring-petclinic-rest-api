package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> a.id().compareTo(b.id()))
            .collect(Collectors.toList());
    }
}
