package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.mapper.VetMapperImpl;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = new VetMapperImpl();

    @Test
    void toResponseDto_mapsFieldsCorrectly() {
        Specialty radiology = new Specialty(1, "radiology");
        Specialty surgery = new Specialty(2, "surgery");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(2);
        assertThat(dto.specialties().get(0).id()).isEqualTo(1);
        assertThat(dto.specialties().get(1).id()).isEqualTo(2);
    }

    @Test
    void toResponseDto_nullSpecialties_returnsEmptyList() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_emptySpecialties_returnsEmptyList() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtoList_mapsList() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new HashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = mapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void mapSpecialties_sortsByIdAscending() {
        Specialty s3 = new Specialty(3, "dentistry");
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> result = mapper.mapSpecialties(Set.of(s3, s1, s2));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2);
        assertThat(result.get(2).id()).isEqualTo(3);
    }
}
