package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(s);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtos_mapsList() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(List.of(s1, s2));
        assertThat(dtos).hasSize(2);
    }

    @Test
    void toEntity_mapsRequestDto() {
        SpecialtyRequestDto req = new SpecialtyRequestDto();
        req.setName("dentistry");

        Specialty entity = specialtyMapper.toEntity(req);

        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isNull();
    }
}
