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

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(specialtySetToList(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> specialtySetToList(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .toList();
    }
}
