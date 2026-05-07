package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    @Mapping(target = "specialties", expression = "java(mapSortedSpecialties(entity.getSpecialties()))")
    VetResponseDto toResponseDto(Vet entity);

    List<VetResponseDto> toResponseDtos(Collection<Vet> entities);

    default List<SpecialtyResponseDto> mapSortedSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        return specialties.stream()
            .sorted(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER))
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .toList();
    }
}
