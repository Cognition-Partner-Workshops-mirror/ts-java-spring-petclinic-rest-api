package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty entity = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("surgery");

        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty(1, "old");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo("updated");
        assertThat(entity.getId()).isEqualTo(1);
    }
}
