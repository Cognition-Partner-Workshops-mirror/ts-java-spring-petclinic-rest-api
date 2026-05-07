package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VetMapper {

    default VetResponseDto toResponse(Vet vet) {
        if (vet == null) {
            return null;
        }
        List<SpecialtyResponseDto> specialties = mapSpecialties(vet.getSpecialties());
        return new VetResponseDto(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties);
    }

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> a.id().compareTo(b.id()))
            .collect(Collectors.toList());
    }

    default List<VetResponseDto> toResponseList(List<Vet> vets) {
        if (vets == null) {
            return List.of();
        }
        return vets.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
