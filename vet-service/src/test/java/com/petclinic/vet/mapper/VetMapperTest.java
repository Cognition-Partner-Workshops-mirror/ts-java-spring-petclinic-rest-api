package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
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
 * Tests for the MapStruct-generated VetMapper implementation.
 * Verifies entity-to-DTO and DTO-to-entity conversions including specialty mapping.
 */
@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty radiology = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullSpecialties_returnsEmptyList() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toResponseDto_nullInput_returnsNull() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new HashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDtoList_nullInput_returnsNull() {
        assertThat(vetMapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyResponseDto radiologyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(radiologyDto));

        Vet entity = vetMapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        // Specialties are ignored in toEntity (handled by service)
        assertThat(entity.getSpecialties()).isEmpty();
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(vetMapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntityFromDto_updatesFields() {
        Vet entity = new Vet(1, "James", "Carter");
        VetRequestDto dto = new VetRequestDto("Helen", "Leary", List.of());

        vetMapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getFirstName()).isEqualTo("Helen");
        assertThat(entity.getLastName()).isEqualTo("Leary");
    }

    @Test
    void specialtiesToDtoList_sortsAlphabetically() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(2, "surgery"));
        specialties.add(new Specialty(1, "dentistry"));

        List<SpecialtyResponseDto> dtos = vetMapper.specialtiesToDtoList(specialties);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("dentistry");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void specialtiesToDtoList_handlesNullNames() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, null));
        specialties.add(new Specialty(2, "surgery"));

        List<SpecialtyResponseDto> dtos = vetMapper.specialtiesToDtoList(specialties);

        assertThat(dtos).hasSize(2);
        // Null name should sort to end
        assertThat(dtos.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void specialtiesToDtoList_nullInput_returnsEmptyList() {
        List<SpecialtyResponseDto> dtos = vetMapper.specialtiesToDtoList(null);
        assertThat(dtos).isEmpty();
    }
}
