package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO classes.
 * Verifies constructors, getters, and setters for VetDto, SpecialtyDto,
 * VetRequestDto, and SpecialtyRequestDto.
 */
class DtoTest {

    // --- VetDto tests ---

    @Test
    void vetDto_defaultConstructor_hasNullFields() {
        VetDto dto = new VetDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isNull();
    }

    @Test
    void vetDto_allArgsConstructor_setsFields() {
        List<SpecialtyDto> specialties = List.of(new SpecialtyDto(1, "radiology"));
        VetDto dto = new VetDto(1, "James", "Carter", specialties);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetDto_setters_updateFields() {
        VetDto dto = new VetDto();
        dto.setId(2);
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialties(List.of());

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    // --- SpecialtyDto tests ---

    @Test
    void specialtyDto_defaultConstructor_hasNullFields() {
        SpecialtyDto dto = new SpecialtyDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void specialtyDto_allArgsConstructor_setsFields() {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyDto_setters_updateFields() {
        SpecialtyDto dto = new SpecialtyDto();
        dto.setId(2);
        dto.setName("surgery");

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    // --- VetRequestDto tests ---

    @Test
    void vetRequestDto_defaultConstructor_hasNullFields() {
        VetRequestDto dto = new VetRequestDto();

        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialtyIds()).isNull();
    }

    @Test
    void vetRequestDto_allArgsConstructor_setsFields() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(1, 2));

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialtyIds()).containsExactly(1, 2);
    }

    @Test
    void vetRequestDto_setters_updateFields() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialtyIds(List.of(1));

        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialtyIds()).containsExactly(1);
    }

    // --- SpecialtyRequestDto tests ---

    @Test
    void specialtyRequestDto_defaultConstructor_hasNullName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();

        assertThat(dto.getName()).isNull();
    }

    @Test
    void specialtyRequestDto_argConstructor_setsName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");

        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyRequestDto_setter_updatesName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("surgery");

        assertThat(dto.getName()).isEqualTo("surgery");
    }
}
