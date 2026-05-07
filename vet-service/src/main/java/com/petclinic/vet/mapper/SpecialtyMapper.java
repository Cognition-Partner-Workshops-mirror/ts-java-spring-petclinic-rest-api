package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.springframework.stereotype.Component;

/**
 * Manual mapper converting Specialty JPA entities to response DTOs.
 */
@Component
public class SpecialtyMapper {

    // Convert a Specialty entity to a SpecialtyResponseDto
    public SpecialtyResponseDto toResponseDto(Specialty entity) {
        return new SpecialtyResponseDto(entity.getId(), entity.getName());
    }
}
