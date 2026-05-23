package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ProblemDetailDto ensuring all fields are correctly set and retrieved.
 */
class ProblemDetailDtoTest {

    @Test
    void constructor_setsAllFields() {
        Instant now = Instant.now();
        ValidationMessageDto validationMsg = new ValidationMessageDto("Field 'name' is required");
        ProblemDetailDto dto = new ProblemDetailDto(
            "http://localhost:8081/api/vets",
            "ResourceNotFoundException",
            404,
            "Vet not found with id: 99",
            now,
            List.of(validationMsg)
        );

        assertThat(dto.getType()).isEqualTo("http://localhost:8081/api/vets");
        assertThat(dto.getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(dto.getStatus()).isEqualTo(404);
        assertThat(dto.getDetail()).isEqualTo("Vet not found with id: 99");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSchemaValidationErrors()).hasSize(1);
    }

    @Test
    void defaultConstructor_andSetters() {
        ProblemDetailDto dto = new ProblemDetailDto();
        Instant now = Instant.now();

        dto.setType("http://localhost:8081/api/specialties");
        dto.setTitle("MethodArgumentNotValidException");
        dto.setStatus(400);
        dto.setDetail("Validation failed");
        dto.setTimestamp(now);
        dto.setSchemaValidationErrors(Collections.emptyList());

        assertThat(dto.getType()).isEqualTo("http://localhost:8081/api/specialties");
        assertThat(dto.getTitle()).isEqualTo("MethodArgumentNotValidException");
        assertThat(dto.getStatus()).isEqualTo(400);
        assertThat(dto.getDetail()).isEqualTo("Validation failed");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSchemaValidationErrors()).isEmpty();
    }
}
