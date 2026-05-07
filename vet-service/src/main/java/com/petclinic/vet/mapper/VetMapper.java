package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    VetResponse toResponse(Vet entity);
}
