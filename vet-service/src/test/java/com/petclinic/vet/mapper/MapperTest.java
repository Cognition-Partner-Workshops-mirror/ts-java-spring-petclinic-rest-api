package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for MapStruct mappers to cover generated code paths.
 */
@SpringBootTest
@ActiveProfiles("test")
class MapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Autowired
    private VetMapper vetMapper;

    @Test
    void specialtyMapper_toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");
        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toResponseDto_nullInput_returnsNull() {
        assertThat(specialtyMapper.toResponseDto(null)).isNull();
    }

    @Test
    void specialtyMapper_toEntity_mapsCorrectly() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty entity = specialtyMapper.toEntity(request);
        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void specialtyMapper_toEntity_nullInput_returnsNull() {
        assertThat(specialtyMapper.toEntity(null)).isNull();
    }

    @Test
    void specialtyMapper_updateEntityFromDto_updatesName() {
        Specialty existing = new Specialty(1, "old name");
        SpecialtyRequestDto update = new SpecialtyRequestDto("new name");
        specialtyMapper.updateEntityFromDto(update, existing);
        assertThat(existing.getName()).isEqualTo("new name");
        assertThat(existing.getId()).isEqualTo(1);
    }

    @Test
    void vetMapper_toResponseDto_mapsVetWithSpecialties() {
        Specialty radiology = new Specialty(1, "radiology");
        Specialty surgery = new Specialty(2, "surgery");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology, surgery));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(2);
        // Verify sorting by id
        assertThat(dto.getSpecialties().get(0).getId()).isEqualTo(1);
        assertThat(dto.getSpecialties().get(1).getId()).isEqualTo(2);
    }

    @Test
    void vetMapper_toResponseDto_nullInput_returnsNull() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void vetMapper_toResponseDto_nullSpecialties_returnsEmptyList() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);
        VetResponseDto dto = vetMapper.toResponseDto(vet);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetMapper_mapSpecialties_emptySet_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(new HashSet<>());
        assertThat(result).isEmpty();
    }
}
