package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
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
    void toEntity_mapsNameCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void toResponseDto_mapsFieldsCorrectly() {
        Specialty entity = new Specialty("radiology");
        entity.setId(1);

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtos_mapsList() {
        Specialty s1 = new Specialty("radiology");
        s1.setId(1);
        Specialty s2 = new Specialty("surgery");
        s2.setId(2);

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void updateEntity_updatesNameOnly() {
        Specialty entity = new Specialty("radiology");
        entity.setId(1);
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");

        specialtyMapper.updateEntity(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("updated-radiology");
    }
}
