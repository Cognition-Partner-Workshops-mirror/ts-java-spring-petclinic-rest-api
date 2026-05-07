package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toEntity_mapsNameAndIgnoresId() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("surgery");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtos_mapsList() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(Arrays.asList(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        assertThat(specialtyMapper.toEntity(null)).isNull();
    }

    @Test
    void toResponseDto_nullEntity_returnsNull() {
        assertThat(specialtyMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtos_nullList_returnsNull() {
        assertThat(specialtyMapper.toResponseDtos(null)).isNull();
    }
}
