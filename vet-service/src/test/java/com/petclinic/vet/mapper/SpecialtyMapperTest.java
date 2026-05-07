package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toResponseDto_mapsFields() {
        Specialty entity = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullEntity_returnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsList() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> result = mapper.toResponseDtoList(List.of(s1, s2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
        assertThat(result.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_nullList_returnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsNameOnly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty(1, "radiology");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        mapper.updateEntity(new SpecialtyRequestDto("surgery"), entity);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isEqualTo(1);
    }
}
