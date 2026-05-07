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

    default List<VetResponseDto> toResponseDtoList(List<Vet> vets) {
        if (vets == null) {
            return new ArrayList<>();
        }
        List<VetResponseDto> list = new ArrayList<>(vets.size());
        for (Vet vet : vets) {
            list.add(toResponseDto(vet));
        }
        return list;
    }

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
            .sorted(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER))
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .toList();
    }
}
