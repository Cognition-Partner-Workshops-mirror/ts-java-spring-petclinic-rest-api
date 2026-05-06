package com.petclinic.vet.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_gettersSetters() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        specialty.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        specialty.setVets(new HashSet<>());

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
        assertThat(specialty.getVets()).isEmpty();
    }

    @Test
    void specialty_constructor() {
        Specialty specialty = new Specialty(1, "surgery");

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("surgery");
    }

    @Test
    void specialty_onCreateSetsTimestamps() {
        Specialty specialty = new Specialty();
        specialty.onCreate();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void specialty_onUpdateSetsTimestamp() {
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
        vet.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        vet.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        vet.setSpecialties(new HashSet<>());

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void vet_constructor() {
        Vet vet = new Vet(1, "Helen", "Leary");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
    }

    @Test
    void vet_onCreateSetsTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_onUpdateSetsTimestamp() {
        Vet vet = new Vet();
        vet.onCreate();
        LocalDateTime originalUpdatedAt = vet.getUpdatedAt();

        vet.onUpdate();

        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void vet_specialtiesRelationship() {
        Vet vet = new Vet(1, "James", "Carter");
        Specialty spec = new Specialty(1, "radiology");

        Set<Specialty> specialties = new HashSet<>();
        specialties.add(spec);
        vet.setSpecialties(specialties);

        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }
}
