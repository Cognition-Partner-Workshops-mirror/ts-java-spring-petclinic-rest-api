package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.mapper.VetMapperImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper vetMapper = new VetMapperImpl();

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty rad = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(rad));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullReturnsNull() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_nullReturnsNull() {
        assertThat(vetMapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsAll() {
        Vet vet1 = new Vet(1, "A", "B");
        Vet vet2 = new Vet(2, "C", "D");

        List<VetResponseDto> result = vetMapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(result).hasSize(2);
    }

    @Test
    void mapSpecialties_nullReturnsEmptyList() {
        assertThat(vetMapper.mapSpecialties(null)).isEmpty();
    }

    @Test
    void mapSpecialties_sortedById() {
        Specialty s1 = new Specialty(2, "surgery");
        Specialty s2 = new Specialty(1, "radiology");

        var result = vetMapper.mapSpecialties(Set.of(s1, s2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2);
    }
}
