package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullReturnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDto_nullSpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        List<VetResponseDto> result = mapper.toResponseDtoList(List.of(vet));

        assertThat(result).hasSize(1);
    }

    @Test
    void toResponseDtoList_nullReturnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }
}
