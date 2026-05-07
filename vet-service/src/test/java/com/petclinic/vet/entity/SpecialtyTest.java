package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void defaultConstructor_shouldCreateEmptySpecialty() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
        assertThat(specialty.getCreatedAt()).isNull();
        assertThat(specialty.getUpdatedAt()).isNull();
    }

    @Test
    void parameterizedConstructor_shouldSetFields() {
        Specialty specialty = new Specialty(1, "radiology");
        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(3);
        specialty.setName("dentistry");

        Instant now = Instant.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(3);
        assertThat(specialty.getName()).isEqualTo("dentistry");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }
}
