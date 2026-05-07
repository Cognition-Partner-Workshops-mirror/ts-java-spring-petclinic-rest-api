package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper vetMapper = Mappers.getMapper(VetMapper.class);

    @Test
    void mapSpecialties_withSpecialties_returnsSortedList() {
        Specialty s1 = new Specialty(2, "surgery");
        Specialty s2 = new Specialty(1, "radiology");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s1);
        specialties.add(s2);

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(specialties);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2);
    }

    @Test
    void mapSpecialties_withNull_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(null);
        assertThat(result).isEmpty();
    }

    @Test
    void mapSpecialties_withEmpty_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(new HashSet<>());
        assertThat(result).isEmpty();
    }

    @Test
    void toResponseDto_mapsAllFields() {
        Vet vet = new Vet(1, "James", "Carter");
        Specialty radiology = new Specialty(1, "radiology");
        vet.setSpecialties(Set.of(radiology));

        var dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }
}
