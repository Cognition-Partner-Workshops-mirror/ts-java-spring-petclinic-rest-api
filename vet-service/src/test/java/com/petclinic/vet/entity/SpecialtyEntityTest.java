package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Specialty entity getters/setters and constructors.
 */
class SpecialtyEntityTest {

    @Test
    void defaultConstructor_createsEmptyEntity() {
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

        specialty.setId(5);
        specialty.setName("surgery");
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(5);
        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }
}
