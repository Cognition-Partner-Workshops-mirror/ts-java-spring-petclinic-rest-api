package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void specialtyDto_accessors() {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void vetDto_accessors() {
        SpecialtyDto spec = new SpecialtyDto(1, "radiology");
        VetDto dto = new VetDto(1, "James", "Carter", List.of(spec));
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
    }

    @Test
    void validationMessageDto_accessors() {
        ValidationMessageDto dto = new ValidationMessageDto("error");
        assertThat(dto.message()).isEqualTo("error");
    }

    @Test
    void problemDetailDto_accessors() {
        Instant now = Instant.now();
        ValidationMessageDto msg = new ValidationMessageDto("err");
        ProblemDetailDto dto = new ProblemDetailDto(
            "http://example.com", "Error", 400, "detail", now, List.of(msg));
        assertThat(dto.type()).isEqualTo("http://example.com");
        assertThat(dto.title()).isEqualTo("Error");
        assertThat(dto.status()).isEqualTo(400);
        assertThat(dto.detail()).isEqualTo("detail");
        assertThat(dto.timestamp()).isEqualTo(now);
        assertThat(dto.schemaValidationErrors()).hasSize(1);
    }

    @Test
    void problemDetailDto_nullErrors() {
        ProblemDetailDto dto = new ProblemDetailDto(
            "http://example.com", "Error", 500, "detail", Instant.now(), null);
        assertThat(dto.schemaValidationErrors()).isNull();
    }
}
