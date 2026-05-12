package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Vet entity and DTOs.
 * Handles the Set<Specialty> to List<SpecialtyResponseDto> conversion.
 */
@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface VetMapper {

    // Convert entity to response DTO, mapping specialty set to sorted list
    @Mapping(target = "specialties", expression = "java(specialtiesToDtoList(vet.getSpecialties()))")
    VetResponseDto toResponseDto(Vet vet);

    // Convert list of entities to list of response DTOs
    List<VetResponseDto> toResponseDtoList(List<Vet> vets);

    // Convert request DTO to entity, ignoring specialties (handled in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequestDto dto);

    // Update existing entity from request DTO, ignoring specialties (handled in service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(VetRequestDto dto, @MappingTarget Vet entity);

    /**
     * Convert a Set of Specialty entities to a sorted List of SpecialtyResponseDto.
     * Sorted by specialty name for consistent ordering.
     */
    default List<SpecialtyResponseDto> specialtiesToDtoList(Set<Specialty> specialties) {
        if (specialties == null) {
            return List.of();
        }
        return specialties.stream()
            .map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
            .sorted((a, b) -> {
                if (a.getName() == null && b.getName() == null) return 0;
                if (a.getName() == null) return 1;
                if (b.getName() == null) return -1;
                return a.getName().compareTo(b.getName());
            })
            .collect(Collectors.toList());
    }
}
