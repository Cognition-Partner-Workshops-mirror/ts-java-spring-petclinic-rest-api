package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void specialtyRequestDto_gettersSetters() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("radiology");
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_gettersSetters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(1);
        dto.setName("radiology");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_allArgsConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "surgery");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void vetRequestDto_gettersSetters() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialtyIds(Arrays.asList(1, 2));
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialtyIds()).containsExactly(1, 2);
    }

    @Test
    void vetRequestDto_allArgsConstructor() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", Arrays.asList(1));
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialtyIds()).containsExactly(1);
    }

    @Test
    void vetRequestDto_nullSpecialtyIds_defaultsToEmptyList() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", null);
        assertThat(dto.getSpecialtyIds()).isEmpty();
    }

    @Test
    void vetResponseDto_gettersSetters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(Collections.emptyList());
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_allArgsConstructor() {
        List<SpecialtyResponseDto> specialties = Arrays.asList(new SpecialtyResponseDto(1, "radiology"));
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", specialties);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_nullSpecialties_defaultsToEmptyList() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void validationMessageDto_gettersSetters() {
        ValidationMessageDto dto = new ValidationMessageDto();
        dto.setMessage("error");
        assertThat(dto.getMessage()).isEqualTo("error");
    }

    @Test
    void validationMessageDto_allArgsConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto("validation error");
        assertThat(dto.getMessage()).isEqualTo("validation error");
    }

    @Test
    void problemDetailDto_gettersSetters() {
        ProblemDetailDto dto = new ProblemDetailDto();
        OffsetDateTime now = OffsetDateTime.now();
        dto.setType("http://example.com");
        dto.setTitle("Error");
        dto.setStatus(400);
        dto.setDetail("Bad request");
        dto.setTimestamp(now);
        dto.setSchemaValidationErrors(Arrays.asList(new ValidationMessageDto("err")));

        assertThat(dto.getType()).isEqualTo("http://example.com");
        assertThat(dto.getTitle()).isEqualTo("Error");
        assertThat(dto.getStatus()).isEqualTo(400);
        assertThat(dto.getDetail()).isEqualTo("Bad request");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSchemaValidationErrors()).hasSize(1);
    }

    @Test
    void problemDetailDto_allArgsConstructor() {
        OffsetDateTime now = OffsetDateTime.now();
        ProblemDetailDto dto = new ProblemDetailDto("http://example.com", "Error", 500,
            "Internal error", now, Collections.emptyList());
        assertThat(dto.getType()).isEqualTo("http://example.com");
        assertThat(dto.getStatus()).isEqualTo(500);
    }

    @Test
    void problemDetailDto_nullErrors_defaultsToEmptyList() {
        OffsetDateTime now = OffsetDateTime.now();
        ProblemDetailDto dto = new ProblemDetailDto("http://example.com", "Error", 500,
            "Internal error", now, null);
        assertThat(dto.getSchemaValidationErrors()).isEmpty();
    }
}
