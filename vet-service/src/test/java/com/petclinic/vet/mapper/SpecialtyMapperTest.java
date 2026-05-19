package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SpecialtyMapper.
 */
class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    @DisplayName("toResponseDto maps entity to response DTO")
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("toEntity maps request DTO to entity")
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        Specialty entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    @DisplayName("updateEntityFromDto updates existing entity")
    void updateEntityFromDto_updatesExisting() {
        Specialty existing = new Specialty(1, "radiology");
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-name");

        mapper.updateEntityFromDto(request, existing);

        assertThat(existing.getName()).isEqualTo("updated-name");
        assertThat(existing.getId()).isEqualTo(1);
    }
}
