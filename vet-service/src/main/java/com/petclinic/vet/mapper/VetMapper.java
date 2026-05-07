package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VetMapper {

    default VetResponseDto toResponseDto(Vet entity) {
        if (entity == null) {
            return null;
        }
        List<SpecialtyResponseDto> specialties = new ArrayList<>();
        if (entity.getSpecialties() != null) {
            for (Specialty s : entity.getSpecialties()) {
                specialties.add(new SpecialtyResponseDto(s.getId(), s.getName()));
            }
        }
        return new VetResponseDto(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            specialties
        );
    }

    default List<VetResponseDto> toResponseDtoList(List<Vet> entities) {
        if (entities == null) {
            return null;
        }
        List<VetResponseDto> list = new ArrayList<>();
        for (Vet vet : entities) {
            list.add(toResponseDto(vet));
        }
        return list;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);
}
