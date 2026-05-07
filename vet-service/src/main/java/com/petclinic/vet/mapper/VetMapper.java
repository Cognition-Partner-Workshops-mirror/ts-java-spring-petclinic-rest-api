package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(mapSpecialtiesToSortedList(entity))")
    VetDto toDto(Vet entity);

    List<VetDto> toDtoList(List<Vet> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    void updateEntity(VetRequestDto dto, @MappingTarget Vet entity);

    default List<SpecialtyDto> mapSpecialtiesToSortedList(Vet entity) {
        if (entity.getSpecialties() == null) {
            return new ArrayList<>();
        }
        return entity.getSpecialties().stream()
            .map(s -> new SpecialtyDto(s.getId(), s.getName()))
            .sorted(Comparator.comparing(SpecialtyDto::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    default Set<Specialty> mapSpecialtyDtoListToSet(List<SpecialtyDto> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        Set<Specialty> set = new HashSet<>();
        for (SpecialtyDto dto : dtos) {
            Specialty s = new Specialty();
            s.setId(dto.getId());
            s.setName(dto.getName());
            set.add(s);
        }
        return set;
    }
}
