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

@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsFields() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtoList_mapsAll() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("radiology");
        assertThat(dtos.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toEntity_mapsNameIgnoresId() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");

        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("dentistry");
    }

    @Test
    void updateEntity_updatesName() {
        Specialty specialty = new Specialty(1, "radiology");
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        specialtyMapper.updateEntity(request, specialty);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("surgery");
    }
}
