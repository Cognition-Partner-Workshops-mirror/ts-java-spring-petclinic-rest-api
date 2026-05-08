package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
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
 * Integration tests for MapStruct mappers.
 * Verifies correct mapping between entities and DTOs.
 */
@SpringBootTest
@ActiveProfiles("test")
class MapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Autowired
    private VetMapper vetMapper;

    @Test
    void specialtyMapper_toResponseDto() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toResponseDto_nullInput() {
        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void specialtyMapper_toEntity() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void specialtyMapper_toEntity_nullInput() {
        Specialty entity = specialtyMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    void specialtyMapper_updateEntityFromDto() {
        Specialty existing = new Specialty(1, "radiology");
        SpecialtyRequestDto update = new SpecialtyRequestDto("surgery");

        specialtyMapper.updateEntityFromDto(update, existing);

        assertThat(existing.getName()).isEqualTo("surgery");
        assertThat(existing.getId()).isEqualTo(1);
    }

    @Test
    void vetMapper_toResponseDto() {
        Vet vet = new Vet(1, "James", "Carter");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));
        vet.setSpecialties(specialties);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void vetMapper_toResponseDto_nullInput() {
        VetResponseDto dto = vetMapper.toResponseDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void vetMapper_toResponseDto_nullSpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetMapper_toResponseDto_emptySpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetMapper_toEntity() {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specialtyDto));

        Vet entity = vetMapper.toEntity(request);

        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void vetMapper_toEntity_nullInput() {
        Vet entity = vetMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    void vetMapper_updateEntityFromDto() {
        Vet existing = new Vet(1, "James", "Carter");
        VetRequestDto update = new VetRequestDto("Helen", "Leary", List.of());

        vetMapper.updateEntityFromDto(update, existing);

        assertThat(existing.getFirstName()).isEqualTo("Helen");
        assertThat(existing.getLastName()).isEqualTo("Leary");
        assertThat(existing.getId()).isEqualTo(1);
    }

    @Test
    void vetMapper_specialtiesToDtoList_withNullInput() {
        List<SpecialtyResponseDto> result = vetMapper.specialtiesToDtoList(null);

        assertThat(result).isEmpty();
    }

    @Test
    void vetMapper_specialtiesToDtoList_withEntries() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));
        specialties.add(new Specialty(2, "surgery"));

        List<SpecialtyResponseDto> result = vetMapper.specialtiesToDtoList(specialties);

        assertThat(result).hasSize(2);
    }
}
