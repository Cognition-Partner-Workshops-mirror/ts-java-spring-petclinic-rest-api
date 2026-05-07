package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(entity.getSpecialties()))")
    VetResponse toResponse(Vet entity);

    List<VetResponse> toResponseList(List<Vet> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequest request);

    default List<SpecialtyResponse> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<Specialty> sorted = new ArrayList<>(specialties);
        sorted.sort(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted.stream()
            .map(s -> new SpecialtyResponse(s.getId(), s.getName()))
            .toList();
    }
}
