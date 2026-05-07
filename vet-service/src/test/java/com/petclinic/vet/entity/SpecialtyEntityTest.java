package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void gettersAndSetters() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        Instant now = Instant.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void constructorWithArgs() {
        Specialty specialty = new Specialty(5, "surgery");
        assertThat(specialty.getId()).isEqualTo(5);
        assertThat(specialty.getName()).isEqualTo("surgery");
    }
}
