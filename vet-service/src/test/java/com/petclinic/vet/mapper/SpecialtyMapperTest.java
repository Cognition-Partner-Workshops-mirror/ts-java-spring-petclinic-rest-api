package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper specialtyMapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty specialty = new Specialty("radiology");
        specialty.setId(1);

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsNameOnly() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty existing = new Specialty("radiology");
        existing.setId(1);

        SpecialtyRequestDto update = new SpecialtyRequestDto("updated-radiology");
        specialtyMapper.updateEntity(update, existing);

        assertThat(existing.getName()).isEqualTo("updated-radiology");
        assertThat(existing.getId()).isEqualTo(1);
    }
}
