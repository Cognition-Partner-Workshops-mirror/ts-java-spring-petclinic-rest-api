package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponse toResponse(Vet vet);

    List<VetResponse> toResponseList(List<Vet> vets);

    default List<SpecialtyResponse> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .sorted(Comparator.comparing(Specialty::getId))
            .map(s -> new SpecialtyResponse(s.getId(), s.getName()))
            .collect(Collectors.toList());
    }
}
