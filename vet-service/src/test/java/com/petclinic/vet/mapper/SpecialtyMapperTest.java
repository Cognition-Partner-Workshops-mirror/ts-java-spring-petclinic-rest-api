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
    void toResponseDto_mapsAllFields() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullInput_returnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsAll() {
        List<Specialty> entities = List.of(
            new Specialty(1, "radiology"),
            new Specialty(2, "surgery")
        );
        List<SpecialtyResponseDto> dtos = mapper.toResponseDtoList(entities);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_nullInput_returnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsNameIgnoresId() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");
        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("oncology");
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesNameOnly() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");

        mapper.updateEntity(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("updated");
    }
}
