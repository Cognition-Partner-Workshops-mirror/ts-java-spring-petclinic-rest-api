package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    default VetResponse toResponse(Vet entity) {
        if (entity == null) {
            return null;
        }
        List<SpecialtyResponse> specialties = mapSpecialties(entity.getSpecialties());
        return new VetResponse(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            specialties
        );
    }

    default List<SpecialtyResponse> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<SpecialtyResponse> list = new ArrayList<>();
        for (Specialty s : specialties) {
            list.add(new SpecialtyResponse(s.getId(), s.getName()));
        }
        list.sort(Comparator.comparing(SpecialtyResponse::id, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    default List<VetResponse> toResponseList(List<Vet> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
