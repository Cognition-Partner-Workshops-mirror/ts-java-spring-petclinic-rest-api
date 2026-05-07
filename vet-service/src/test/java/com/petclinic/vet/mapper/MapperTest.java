package com.petclinic.vet.mapper;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Autowired
    private VetMapper vetMapper;

    @Test
    void specialtyMapper_toResponseDto() {
        Specialty entity = new Specialty(1, "radiology");
        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toResponseDto_null() {
        assertThat(specialtyMapper.toResponseDto(null)).isNull();
    }

    @Test
    void specialtyMapper_toResponseDtoList() {
        List<Specialty> entities = List.of(
            new Specialty(1, "radiology"),
            new Specialty(2, "surgery")
        );
        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtoList(entities);
        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtyMapper_toResponseDtoList_null() {
        assertThat(specialtyMapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void specialtyMapper_toEntity() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");
        Specialty entity = specialtyMapper.toEntity(dto);
        assertThat(entity.getName()).isEqualTo("oncology");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void specialtyMapper_toEntity_null() {
        assertThat(specialtyMapper.toEntity(null)).isNull();
    }

    @Test
    void specialtyMapper_updateEntity() {
        Specialty entity = new Specialty(1, "old");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");
        specialtyMapper.updateEntity(dto, entity);
        assertThat(entity.getName()).isEqualTo("updated");
    }

    @Test
    void vetMapper_toResponseDto() {
        Specialty spec = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter", List.of(spec));
        VetResponseDto dto = vetMapper.toResponseDto(vet);
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.specialties()).hasSize(1);
    }

    @Test
    void vetMapper_toResponseDto_null() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void vetMapper_toResponseDtoList() {
        Vet vet1 = new Vet(1, "James", "Carter", List.of());
        Vet vet2 = new Vet(2, "Helen", "Leary", List.of());
        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(List.of(vet1, vet2));
        assertThat(dtos).hasSize(2);
    }

    @Test
    void vetMapper_toResponseDtoList_null() {
        assertThat(vetMapper.toResponseDtoList(null)).isNull();
    }
}
