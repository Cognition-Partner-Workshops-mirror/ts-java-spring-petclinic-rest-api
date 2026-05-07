package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.mapper.SpecialtyMapperImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = new SpecialtyMapperImpl();

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullReturnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsAll() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> result = mapper.toResponseDtoList(List.of(s1, s2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
        assertThat(result.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_nullReturnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsFromRequestDto() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("dentistry");
        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_nullReturnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesExisting() {
        Specialty existing = new Specialty(1, "radiology");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("cardiology");

        mapper.updateEntity(dto, existing);

        assertThat(existing.getName()).isEqualTo("cardiology");
        assertThat(existing.getId()).isEqualTo(1);
    }
}
