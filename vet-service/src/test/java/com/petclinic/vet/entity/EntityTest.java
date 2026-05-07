package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_gettersSetters() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        LocalDateTime now = LocalDateTime.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void specialty_onCreate_setsTimestamps() {
        Specialty specialty = new Specialty();
        specialty.onCreate();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
        assertThat(specialty.getCreatedAt()).isEqualTo(specialty.getUpdatedAt());
    }

    @Test
    void specialty_onUpdate_setsUpdatedAt() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        LocalDateTime originalUpdatedAt = specialty.getUpdatedAt();

        specialty.onUpdate();

        assertThat(specialty.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void vet_gettersSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        LocalDateTime now = LocalDateTime.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        Set<Specialty> specialties = new HashSet<>();
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        specialties.add(s);
        vet.setSpecialties(specialties);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_onCreate_setsTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_onUpdate_setsUpdatedAt() {
        Vet vet = new Vet();
        vet.onCreate();
        LocalDateTime originalUpdatedAt = vet.getUpdatedAt();

        vet.onUpdate();

        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }
}
