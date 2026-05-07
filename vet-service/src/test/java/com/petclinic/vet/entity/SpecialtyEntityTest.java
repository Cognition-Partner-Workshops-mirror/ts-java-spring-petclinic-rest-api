package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void shouldSetAndGetFields() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldSetAndGetAuditFields() {
        Specialty specialty = new Specialty();
        Instant now = Instant.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void onCreateShouldSetTimestamps() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdateShouldSetUpdatedAt() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        Instant firstUpdate = specialty.getUpdatedAt();
        specialty.onUpdate();
        assertThat(specialty.getUpdatedAt()).isAfterOrEqualTo(firstUpdate);
    }
}
