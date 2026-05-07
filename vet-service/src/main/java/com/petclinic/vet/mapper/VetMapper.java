package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "specialties", source = "specialties")
    VetDto toDto(Vet vet);

    List<VetDto> toDtoList(Iterable<Vet> vets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    Vet toEntity(VetRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    void updateEntity(VetRequestDto dto, @MappingTarget Vet vet);

    default List<com.petclinic.vet.dto.SpecialtyDto> mapSpecialties(Set<com.petclinic.vet.entity.Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new com.petclinic.vet.dto.SpecialtyDto(s.getId(), s.getName()))
            .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.name() != null ? a.name() : "",
                b.name() != null ? b.name() : ""))
            .toList();
    }
}
