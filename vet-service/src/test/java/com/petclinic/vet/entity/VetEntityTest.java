package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Vet JPA entity.
 * Verifies getters/setters, audit lifecycle callbacks, and specialty management helpers.
 */
class VetEntityTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        Vet vet = new Vet();
        Instant now = Instant.now();

        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void onCreate_setsAuditTimestamps() {
        Vet vet = new Vet();

        // Simulate @PrePersist lifecycle callback
        vet.onCreate();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_setsUpdatedAtTimestamp() {
        Vet vet = new Vet();
        vet.onCreate();
        Instant originalUpdatedAt = vet.getUpdatedAt();

        // Simulate @PreUpdate lifecycle callback
        vet.onUpdate();

        assertThat(vet.getUpdatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void addSpecialty_addsToSet() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        vet.addSpecialty(specialty);

        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(specialty);
    }

    @Test
    void clearSpecialties_removesAll() {
        Vet vet = new Vet();
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        vet.setSpecialties(new java.util.HashSet<>(Set.of(s1, s2)));
        assertThat(vet.getSpecialties()).hasSize(2);

        vet.clearSpecialties();

        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void setSpecialties_replacesExistingSet() {
        Vet vet = new Vet();
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet.setSpecialties(Set.of(radiology));

        assertThat(vet.getSpecialties()).hasSize(1);
    }
}
