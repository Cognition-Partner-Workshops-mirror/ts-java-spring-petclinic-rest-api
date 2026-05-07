package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SpecialtyMapper.class)
public interface VetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vet toEntity(VetRequest request);

    default VetResponse toResponse(Vet vet) {
        if (vet == null) {
            return null;
        }
        List<SpecialtyResponse> sortedSpecialties = vet.getSpecialties().stream()
            .sorted(Comparator.comparing(Specialty::getName, String.CASE_INSENSITIVE_ORDER))
            .map(s -> new SpecialtyResponse(s.getId(), s.getName()))
            .toList();
        return new VetResponse(vet.getId(), vet.getFirstName(), vet.getLastName(), sortedSpecialties);
    }

    default List<VetResponse> toResponseList(List<Vet> vets) {
        if (vets == null) {
            return List.of();
        }
        return vets.stream().map(this::toResponse).toList();
    }
}
