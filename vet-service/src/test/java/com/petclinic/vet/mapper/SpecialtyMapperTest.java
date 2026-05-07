package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty specialty = new Specialty("radiology");
        specialty.setId(1);

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsFromRequest() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");

        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntity_updatesExistingEntity() {
        Specialty existing = new Specialty("radiology");
        existing.setId(5);

        SpecialtyRequestDto update = new SpecialtyRequestDto("oncology");

        specialtyMapper.updateEntity(update, existing);

        assertThat(existing.getName()).isEqualTo("oncology");
        assertThat(existing.getId()).isEqualTo(5);
    }
}
