package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", source = "specialties")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    SpecialtyResponseDto specialtyToDto(Specialty specialty);

    default List<SpecialtyResponseDto> specialtySetToList(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(this::specialtyToDto)
            .toList();
    }
}
