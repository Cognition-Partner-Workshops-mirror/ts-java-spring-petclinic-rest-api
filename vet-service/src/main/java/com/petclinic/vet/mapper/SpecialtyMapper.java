package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.request.SpecialtyRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponse toResponse(Specialty specialty);

    Specialty toEntity(SpecialtyRequest request);

    void updateEntity(SpecialtyRequest request, @MappingTarget Specialty specialty);
}
