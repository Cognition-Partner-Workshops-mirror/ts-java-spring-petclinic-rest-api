package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponse toResponse(Specialty entity);

    List<SpecialtyResponse> toResponseList(List<Specialty> entities);

    Specialty toEntity(SpecialtyRequest request);

    void updateEntity(SpecialtyRequest request, @MappingTarget Specialty entity);
}
