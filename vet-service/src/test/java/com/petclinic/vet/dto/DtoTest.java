package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO constructors, getters, and setters.
 */
class DtoTest {

    @Test
    void vetResponseDto_noArgConstructor() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of());

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_allArgConstructor() {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of(spec));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_nullSpecialties_shouldDefaultToEmptyList() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetRequestDto_noArgConstructor() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of());

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
    }

    @Test
    void specialtyRequestDto_noArgConstructor() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("surgery");
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void specialtyResponseDto_noArgConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(1);
        dto.setName("surgery");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void validationMessageDto_noArgConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto();
        dto.setMessage("test message");
        assertThat(dto.getMessage()).isEqualTo("test message");
    }

    @Test
    void validationMessageDto_allArgConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto("error message");
        assertThat(dto.getMessage()).isEqualTo("error message");
    }
}
