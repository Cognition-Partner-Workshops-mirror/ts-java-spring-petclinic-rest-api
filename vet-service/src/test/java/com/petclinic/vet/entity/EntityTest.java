package com.petclinic.vet.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JPA entity getters/setters and constructors.
 */
class EntityTest {

    @Test
    @DisplayName("Specialty getters and setters work correctly")
    void specialty_gettersSetters() {
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
    @DisplayName("Specialty parameterized constructor works")
    void specialty_constructor() {
        Specialty specialty = new Specialty(1, "surgery");

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("surgery");
    }

    @Test
    @DisplayName("Vet getters and setters work correctly")
    void vet_gettersSetters() {
        Vet vet = new Vet();
        Instant now = Instant.now();
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));

        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(specialties);
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Vet parameterized constructor works")
    void vet_constructor() {
        Vet vet = new Vet(1, "James", "Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
