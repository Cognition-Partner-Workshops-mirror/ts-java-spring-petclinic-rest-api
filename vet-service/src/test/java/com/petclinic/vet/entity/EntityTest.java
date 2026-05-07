package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_prePersist_setsTimestamps() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialty.onCreate();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void specialty_preUpdate_updatesTimestamp() throws InterruptedException {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        Instant initialUpdatedAt = specialty.getUpdatedAt();

        Thread.sleep(10);
        specialty.onUpdate();

        assertThat(specialty.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    void specialty_gettersAndSetters() {
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
    void vet_prePersist_setsTimestamps() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.onCreate();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_preUpdate_updatesTimestamp() throws InterruptedException {
        Vet vet = new Vet();
        vet.onCreate();
        Instant initialUpdatedAt = vet.getUpdatedAt();

        Thread.sleep(10);
        vet.onUpdate();

        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    void vet_gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        Set<Specialty> specialties = new HashSet<>();
        vet.setSpecialties(specialties);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void vet_addSpecialty() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        vet.addSpecialty(specialty);

        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_clearSpecialties() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        vet.addSpecialty(specialty);

        vet.clearSpecialties();

        assertThat(vet.getSpecialties()).isEmpty();
    }
}
