package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Specialty JPA entity.
 * Verifies getters/setters and audit lifecycle callbacks.
 */
class SpecialtyEntityTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        Specialty specialty = new Specialty();
        Instant now = Instant.now();

        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void onCreate_setsAuditTimestamps() {
        Specialty specialty = new Specialty();

        // Simulate @PrePersist lifecycle callback
        specialty.onCreate();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_setsUpdatedAtTimestamp() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        Instant originalUpdatedAt = specialty.getUpdatedAt();

        // Simulate @PreUpdate lifecycle callback
        specialty.onUpdate();

        assertThat(specialty.getUpdatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }
}
