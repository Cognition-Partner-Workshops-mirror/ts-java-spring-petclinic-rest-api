package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.entity.VetEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VetMapper {

    default VetResponseDto toResponseDto(VetEntity entity) {
        if (entity == null) {
            return null;
        }
        List<SpecialtyResponseDto> specialties = mapSpecialties(entity.getSpecialties());
        return new VetResponseDto(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            specialties
        );
    }

    default List<SpecialtyResponseDto> mapSpecialties(Set<SpecialtyEntity> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> a.id().compareTo(b.id()))
            .collect(Collectors.toList());
    }
}
