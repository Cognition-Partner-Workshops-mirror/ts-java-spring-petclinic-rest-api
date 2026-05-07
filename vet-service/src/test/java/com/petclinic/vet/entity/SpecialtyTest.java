package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void defaultConstructor_createsEmptySpecialty() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
    }

    @Test
    void parameterizedConstructor_setsFields() {
        Specialty specialty = new Specialty(1, "radiology");
        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Specialty specialty = new Specialty();
        Instant now = Instant.now();

        specialty.setId(3);
        specialty.setName("surgery");
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(3);
        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }
}
