package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Manual mapper converting Vet JPA entities to response DTOs.
 * Specialties are sorted by ID for deterministic ordering.
 */
@Component
public class VetMapper {

    private final SpecialtyMapper specialtyMapper;

    public VetMapper(SpecialtyMapper specialtyMapper) {
        this.specialtyMapper = specialtyMapper;
    }

    // Convert a Vet entity (with loaded specialties) to a VetResponseDto
    public VetResponseDto toResponseDto(Vet entity) {
        List<SpecialtyResponseDto> specialties = entity.getSpecialties().stream()
            .map(specialtyMapper::toResponseDto)
            .sorted(Comparator.comparing(SpecialtyResponseDto::id))
            .toList();

        return new VetResponseDto(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            specialties
        );
    }
}
