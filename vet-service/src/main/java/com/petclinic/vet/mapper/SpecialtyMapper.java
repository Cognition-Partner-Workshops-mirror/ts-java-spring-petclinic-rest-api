package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {

    public SpecialtyResponseDto toResponseDto(Specialty entity) {
        return new SpecialtyResponseDto(entity.getSpecialtyId(), entity.getName());
    }

    public Specialty toEntity(SpecialtyRequestDto dto) {
        Specialty entity = new Specialty();
        entity.setName(dto.getName());
        return entity;
    }

    public void updateEntity(Specialty entity, SpecialtyRequestDto dto) {
        entity.setName(dto.getName());
    }
}
