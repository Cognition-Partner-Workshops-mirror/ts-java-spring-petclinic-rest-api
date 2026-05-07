package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("surgery");

        Specialty specialty = mapper.toEntity(requestDto);

        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getId()).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Specialty specialty = new Specialty(1, "radiology");
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("surgery");

        mapper.updateEntity(requestDto, specialty);

        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getId()).isEqualTo(1);
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> dtos = mapper.toResponseDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }
}
