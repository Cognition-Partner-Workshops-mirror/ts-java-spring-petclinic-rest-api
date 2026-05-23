package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SpecialtyMapper verifying entity-to-DTO and DTO-to-entity conversions.
 */
@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toEntity_mapsFieldsCorrectly() {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");
        Specialty entity = specialtyMapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("radiology");
    }

    @Test
    void toDto_mapsFieldsCorrectly() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyDto dto = specialtyMapper.toDto(entity);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toDtos_mapsCollection() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");
        List<SpecialtyDto> dtos = specialtyMapper.toDtos(List.of(s1, s2));
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        assertThat(specialtyMapper.toEntity(null)).isNull();
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        assertThat(specialtyMapper.toDto(null)).isNull();
    }

    @Test
    void toDtos_nullCollection_returnsNull() {
        assertThat(specialtyMapper.toDtos(null)).isNull();
    }
}
