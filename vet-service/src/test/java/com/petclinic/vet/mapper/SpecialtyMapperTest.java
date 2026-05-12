package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpecialtyMapper} MapStruct-generated implementation.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void shouldMapEntityToResponseDto() {
        Specialty specialty = new Specialty("radiology");
        specialty.setId(1);

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldMapEntityListToResponseDtoList() {
        Specialty s1 = new Specialty("radiology");
        s1.setId(1);
        Specialty s2 = new Specialty("surgery");
        s2.setId(2);

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void shouldMapRequestDtoToEntity() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("dentistry");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void shouldUpdateEntityFromDto() {
        Specialty entity = new Specialty("radiology");
        entity.setId(1);

        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");
        specialtyMapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("updated-radiology");
    }
}
