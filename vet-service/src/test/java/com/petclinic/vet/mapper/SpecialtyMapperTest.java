package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private SpecialtyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SpecialtyMapperImpl();
    }

    @Test
    void toResponseDto_mapsAllFields() {
        SpecialtyEntity entity = new SpecialtyEntity();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_returnsNullForNullEntity() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toEntity_mapsName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("surgery");

        SpecialtyEntity entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_returnsNullForNullDto() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        SpecialtyEntity entity = new SpecialtyEntity();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");
        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo("oncology");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_doesNothingForNullDto() {
        SpecialtyEntity entity = new SpecialtyEntity();
        entity.setId(1);
        entity.setName("radiology");

        mapper.updateEntity(null, entity);

        assertThat(entity.getName()).isEqualTo("radiology");
    }
}
