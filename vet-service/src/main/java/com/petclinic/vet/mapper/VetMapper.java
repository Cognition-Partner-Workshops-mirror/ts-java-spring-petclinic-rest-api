package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", expression = "java(specialtySetToList(entity.getSpecialties()))")
    VetDto toDto(Vet entity);

    List<VetDto> toDtoList(List<Vet> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetDto dto);

    default List<SpecialtyDto> specialtySetToList(Set<Specialty> specialties) {
        if (specialties == null) {
            return new ArrayList<>();
        }
        List<SpecialtyDto> list = new ArrayList<>();
        for (Specialty s : specialties) {
            list.add(new SpecialtyDto(s.getId(), s.getName()));
        }
        return list;
    }
}
