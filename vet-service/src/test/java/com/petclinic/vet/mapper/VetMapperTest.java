package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.entity.VetEntity;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private VetMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VetMapperImpl();
    }

    @Test
    void toResponseDto_mapsAllFields() {
        SpecialtyEntity specialty = new SpecialtyEntity();
        specialty.setId(1);
        specialty.setName("radiology");

        VetEntity entity = new VetEntity();
        entity.setId(10);
        entity.setFirstName("James");
        entity.setLastName("Carter");
        entity.setSpecialties(Set.of(specialty));

        VetResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(10);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).id()).isEqualTo(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_returnsNullForNullEntity() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDto_handlesEmptySpecialties() {
        VetEntity entity = new VetEntity();
        entity.setId(1);
        entity.setFirstName("Helen");
        entity.setLastName("Leary");
        entity.setSpecialties(new HashSet<>());

        VetResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_handlesNullSpecialties() {
        VetEntity entity = new VetEntity();
        entity.setId(1);
        entity.setFirstName("Helen");
        entity.setLastName("Leary");
        entity.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void mapSpecialties_sortsById() {
        SpecialtyEntity s1 = new SpecialtyEntity();
        s1.setId(3);
        s1.setName("dentistry");

        SpecialtyEntity s2 = new SpecialtyEntity();
        s2.setId(1);
        s2.setName("radiology");

        List<SpecialtyResponseDto> result = mapper.mapSpecialties(Set.of(s1, s2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(3);
    }

    @Test
    void mapSpecialties_returnsEmptyListForNull() {
        List<SpecialtyResponseDto> result = mapper.mapSpecialties(null);

        assertThat(result).isEmpty();
    }
}
