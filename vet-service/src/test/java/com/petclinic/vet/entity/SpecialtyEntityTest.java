package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Unit tests for Specialty entity getters, setters, and constructors
class SpecialtyEntityTest {

    @Test
    void defaultConstructor_initializesNullFields() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
        assertThat(specialty.getCreatedAt()).isNull();
        assertThat(specialty.getUpdatedAt()).isNull();
    }

    @Test
    void parameterizedConstructor_setsName() {
        Specialty specialty = new Specialty("radiology");
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getId()).isNull();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("surgery");

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("surgery");
    }

    @Test
    void auditFields_canBeSetAndRead() {
        Specialty specialty = new Specialty("radiology");
        LocalDateTime now = LocalDateTime.now();

        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }
}
