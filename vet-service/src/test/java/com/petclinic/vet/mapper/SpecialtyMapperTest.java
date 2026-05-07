package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_handlesNullInput() {
        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toResponseDtos_mapsCollection() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtos_handlesNullInput() {
        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(null);
        assertThat(dtos).isNull();
    }

    @Test
    void toEntity_mapsRequestFields() {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "radiology");
        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_handlesNullInput() {
        Specialty entity = specialtyMapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntities_mapsCollection() {
        SpecialtyRequestDto r1 = new SpecialtyRequestDto(null, "radiology");
        SpecialtyRequestDto r2 = new SpecialtyRequestDto(null, "surgery");

        List<Specialty> entities = specialtyMapper.toEntities(List.of(r1, r2));

        assertThat(entities).hasSize(2);
        assertThat(entities.get(0).getName()).isEqualTo("radiology");
        assertThat(entities.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toEntities_handlesNullInput() {
        List<Specialty> entities = specialtyMapper.toEntities(null);
        assertThat(entities).isNull();
    }

    @Test
    void updateEntity_updatesExistingEntity() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("old");

        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "updated");
        specialtyMapper.updateEntity(request, entity);

        assertThat(entity.getName()).isEqualTo("updated");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_handlesNullDto() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("original");

        specialtyMapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("original");
    }
}
