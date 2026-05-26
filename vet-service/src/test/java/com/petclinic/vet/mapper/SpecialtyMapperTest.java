package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpecialtyMapper} to verify MapStruct-generated code.
 */
@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    @DisplayName("toResponseDto maps entity fields correctly")
    void toResponseDto_mapsCorrectly() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("toEntity maps request DTO to entity")
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("surgery");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("surgery");
        // ID should be null because it is auto-generated
        assertThat(entity.getId()).isNull();
    }

    @Test
    @DisplayName("updateEntityFromDto updates the entity name in place")
    void updateEntityFromDto_updatesEntity() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");
        specialtyMapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("updated-radiology");
        // ID should remain unchanged
        assertThat(entity.getId()).isEqualTo(1);
    }
}
