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
 * Tests for the MapStruct-generated SpecialtyMapper implementation.
 * Uses SpringBootTest to ensure the MapStruct-generated bean is available.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullInput_returnsNull() {
        assertThat(specialtyMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        List<Specialty> specialties = List.of(
            new Specialty(1, "radiology"),
            new Specialty(2, "surgery")
        );

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtoList(specialties);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtoList_nullInput_returnsNull() {
        assertThat(specialtyMapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("oncology");
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(specialtyMapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntityFromDto_updatesFields() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("cardiology");

        specialtyMapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("cardiology");
    }
}
