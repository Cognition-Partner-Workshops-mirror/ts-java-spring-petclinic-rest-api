package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtos(List<Vet> vets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<Specialty> sorted = new ArrayList<>(specialties);
        sorted.sort(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .toList();
    }
}
