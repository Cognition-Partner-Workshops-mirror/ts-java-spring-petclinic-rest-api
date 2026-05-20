package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the MapStruct-generated SpecialtyMapper implementation.
 * Uses @SpringBootTest to load the Spring context and inject the generated mapper.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toEntity_shouldMapRequestDtoToEntity() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toResponseDto_shouldMapEntityToResponseDto() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtos_fromList_shouldMapListOfEntitiesToDtos() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtos_fromSet_shouldMapSetOfEntitiesToDtos() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(Set.of(s1));

        assertThat(dtos).hasSize(1);
    }

    @Test
    void toResponseDtos_fromList_shouldReturnNullForNullInput() {
        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos((List<Specialty>) null);
        assertThat(dtos).isNull();
    }

    @Test
    void toResponseDtos_fromSet_shouldReturnNullForNullInput() {
        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos((Set<Specialty>) null);
        assertThat(dtos).isNull();
    }

    @Test
    void updateEntityFromDto_shouldUpdateExistingEntity() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");

        specialtyMapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("updated-radiology");
        assertThat(entity.getId()).isEqualTo(1);
    }
}
