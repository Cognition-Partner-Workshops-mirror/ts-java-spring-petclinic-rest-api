package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityTest {

    @Test
    void shouldSetAndGetFields() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldSetAndGetAuditFields() {
        Vet vet = new Vet();
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldManageSpecialties() {
        Vet vet = new Vet();
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        vet.addSpecialty(radiology);
        vet.addSpecialty(surgery);

        assertThat(vet.getSpecialtiesInternal()).hasSize(2);
        List<Specialty> sorted = vet.getSpecialties();
        assertThat(sorted.get(0).getName()).isEqualTo("radiology");
        assertThat(sorted.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void shouldClearSpecialties() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("test");
        vet.addSpecialty(s);
        assertThat(vet.getSpecialtiesInternal()).hasSize(1);

        vet.clearSpecialties();
        assertThat(vet.getSpecialtiesInternal()).isEmpty();
    }

    @Test
    void shouldSetSpecialties() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("test");
        Set<Specialty> specs = new HashSet<>();
        specs.add(s);
        vet.setSpecialties(specs);
        assertThat(vet.getSpecialtiesInternal()).hasSize(1);
    }

    @Test
    void onCreateShouldSetTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();
        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdateShouldSetUpdatedAt() {
        Vet vet = new Vet();
        vet.onCreate();
        Instant firstUpdate = vet.getUpdatedAt();
        vet.onUpdate();
        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(firstUpdate);
    }
}
