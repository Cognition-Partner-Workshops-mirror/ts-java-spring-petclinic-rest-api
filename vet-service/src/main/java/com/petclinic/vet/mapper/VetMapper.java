package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(entity.getSpecialties()))")
    VetResponse toResponse(Vet entity);

    List<VetResponse> toResponseList(List<Vet> entities);

    default List<SpecialtyResponse> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<SpecialtyResponse> result = new ArrayList<>();
        for (Specialty s : specialties) {
            result.add(new SpecialtyResponse(s.getId(), s.getName()));
        }
        return result;
    }
}
