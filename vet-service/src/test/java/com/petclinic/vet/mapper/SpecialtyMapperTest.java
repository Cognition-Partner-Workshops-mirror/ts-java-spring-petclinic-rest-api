package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper specialtyMapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("surgery");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("old");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("new");
        specialtyMapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getId()).isEqualTo(1);
    }
}
