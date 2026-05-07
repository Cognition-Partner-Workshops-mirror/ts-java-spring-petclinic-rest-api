package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {

    public SpecialtyResponse toResponse(Specialty entity) {
        return new SpecialtyResponse(entity.getId(), entity.getName());
    }

    public Specialty toEntity(SpecialtyRequest request) {
        Specialty specialty = new Specialty();
        specialty.setName(request.name());
        return specialty;
    }

    public void updateEntity(Specialty entity, SpecialtyRequest request) {
        entity.setName(request.name());
    }
}
