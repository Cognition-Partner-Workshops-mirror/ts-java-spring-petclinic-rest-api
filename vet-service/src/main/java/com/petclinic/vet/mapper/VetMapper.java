package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<SpecialtyResponseDto> result = new ArrayList<>();
        for (Specialty s : specialties) {
            result.add(new SpecialtyResponseDto(s.getId(), s.getName()));
        }
        result.sort(Comparator.comparing(SpecialtyResponseDto::id));
        return result;
    }
}
