package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {

    public SpecialtyResponseDto toResponseDto(Specialty entity) {
        return new SpecialtyResponseDto(entity.getId(), entity.getName());
    }
}
