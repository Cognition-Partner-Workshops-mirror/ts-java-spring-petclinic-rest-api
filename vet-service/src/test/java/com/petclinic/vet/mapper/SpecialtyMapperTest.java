package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("surgery");

        Specialty specialty = specialtyMapper.toEntity(dto);

        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getId()).isNull();
    }

    @Test
    void updateEntity_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("dentistry");

        specialtyMapper.updateEntity(dto, specialty);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("dentistry");
    }
}
