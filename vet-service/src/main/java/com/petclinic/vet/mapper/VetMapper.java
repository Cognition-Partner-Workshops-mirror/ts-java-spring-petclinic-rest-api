package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(vet))")
    VetResponseDto toResponseDto(Vet vet);

    List<VetResponseDto> toResponseDtos(Collection<Vet> vets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "specialtiesSorted", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> mapSpecialties(Vet vet) {
        return vet.getSpecialtiesSorted().stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .collect(Collectors.toList());
    }
}
