package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Vet;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    private final SpecialtyMapper specialtyMapper;

    public VetMapper(SpecialtyMapper specialtyMapper) {
        this.specialtyMapper = specialtyMapper;
    }

    public VetResponse toResponse(Vet entity) {
        List<SpecialtyResponse> specialties = entity.getSpecialties().stream()
            .map(specialtyMapper::toResponse)
            .sorted(Comparator.comparing(SpecialtyResponse::id))
            .toList();
        return new VetResponse(entity.getId(), entity.getFirstName(), entity.getLastName(), specialties);
    }
}
