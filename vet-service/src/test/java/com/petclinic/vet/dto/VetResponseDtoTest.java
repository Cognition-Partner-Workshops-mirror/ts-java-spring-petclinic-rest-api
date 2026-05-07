package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetResponseDtoTest {

    @Test
    void noArgConstructorDefaults() {
        VetResponseDto dto = new VetResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isNotNull().isEmpty();
    }

    @Test
    void allArgConstructor() {
        List<SpecialtyResponseDto> specs = List.of(new SpecialtyResponseDto(1, "surgery"));
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", specs);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void allArgConstructor_nullSpecialties_defaultsToEmptyList() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isNotNull().isEmpty();
    }

    @Test
    void setters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(2);
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialties(List.of(new SpecialtyResponseDto(1, "radiology")));
        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }
}
