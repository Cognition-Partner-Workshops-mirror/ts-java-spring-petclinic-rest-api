package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private SpecialtyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SpecialtyMapperImpl();
    }

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullInput() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtos_mapsCorrectly() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = mapper.toResponseDtos(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtos_nullInput() {
        assertThat(mapper.toResponseDtos(null)).isNull();
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");

        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("oncology");
    }

    @Test
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_mapsCorrectly() {
        Specialty existing = new Specialty();
        existing.setId(1);
        existing.setName("radiology");
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");
        mapper.updateEntity(dto, existing);

        assertThat(existing.getId()).isEqualTo(1);
        assertThat(existing.getName()).isEqualTo("updated-radiology");
    }

    @Test
    void updateEntity_nullDto_doesNothing() {
        Specialty existing = new Specialty();
        existing.setId(1);
        existing.setName("radiology");

        mapper.updateEntity(null, existing);

        assertThat(existing.getName()).isEqualTo("radiology");
    }
}
