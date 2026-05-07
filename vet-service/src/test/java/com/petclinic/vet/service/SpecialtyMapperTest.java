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
    void toResponseDto_mapsFieldsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullInput_returnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsList() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> dtos = mapper.toResponseDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_nullInput_returnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsFieldsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("cardiology");

        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("cardiology");
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo("updated-radiology");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_nullDto_doesNothing() {
        Specialty entity = new Specialty(1, "radiology");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("radiology");
    }
}
