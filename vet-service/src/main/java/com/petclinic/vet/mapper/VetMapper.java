package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    default VetResponseDto toResponseDto(Vet vet) {
        if (vet == null) {
            return null;
        }
        List<SpecialtyResponseDto> specialties = mapSpecialties(vet.getSpecialties());
        return new VetResponseDto(
            vet.getId(),
            vet.getFirstName(),
            vet.getLastName(),
            specialties
        );
    }

    default List<VetResponseDto> toResponseDtoList(List<Vet> vets) {
        if (vets == null) {
            return null;
        }
        List<VetResponseDto> list = new ArrayList<>(vets.size());
        for (Vet vet : vets) {
            list.add(toResponseDto(vet));
        }
        return list;
    }

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .sorted(Comparator.comparing(Specialty::getId))
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .toList();
    }
}
