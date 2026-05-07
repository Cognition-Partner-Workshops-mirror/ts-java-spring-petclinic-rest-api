package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.response.SpecialtyResponse;
import com.petclinic.vet.dto.response.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponse toResponse(Vet vet);

    default List<SpecialtyResponse> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponse(s.getId(), s.getName()))
            .sorted(Comparator.comparing(SpecialtyResponse::getId))
            .toList();
    }
}
