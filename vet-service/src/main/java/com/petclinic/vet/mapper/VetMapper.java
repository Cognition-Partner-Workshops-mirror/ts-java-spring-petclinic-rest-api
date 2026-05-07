package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", source = "specialties", qualifiedByName = "dtoListToEntitySet")
    Vet toEntity(VetRequestDto dto);

    @Mapping(target = "specialties", source = "specialties", qualifiedByName = "entitySetToDtoList")
    VetResponseDto toResponseDto(Vet entity);

    List<VetResponseDto> toResponseDtos(List<Vet> entities);

    @Named("dtoListToEntitySet")
    default Set<Specialty> dtoListToEntitySet(List<SpecialtyResponseDto> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        Set<Specialty> set = new HashSet<>();
        for (SpecialtyResponseDto dto : dtos) {
            Specialty specialty = new Specialty();
            specialty.setId(dto.getId());
            specialty.setName(dto.getName());
            set.add(specialty);
        }
        return set;
    }

    @Named("entitySetToDtoList")
    default List<SpecialtyResponseDto> entitySetToDtoList(Set<Specialty> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<SpecialtyResponseDto> list = new ArrayList<>();
        for (Specialty entity : entities) {
            list.add(new SpecialtyResponseDto(entity.getId(), entity.getName()));
        }
        list.sort((a, b) -> {
            if (a.getName() == null) return -1;
            if (b.getName() == null) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });
        return list;
    }
}
