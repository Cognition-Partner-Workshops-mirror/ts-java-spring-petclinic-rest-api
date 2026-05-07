package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialties(entity.getSpecialties()))")
    VetResponseDto toResponseDto(Vet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    default List<SpecialtyResponseDto> mapSpecialties(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        List<SpecialtyResponseDto> result = new ArrayList<>();
        for (Specialty s : specialties) {
            result.add(new SpecialtyResponseDto(s.getId(), s.getName()));
        }
        result.sort(Comparator.comparing(SpecialtyResponseDto::name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
}
