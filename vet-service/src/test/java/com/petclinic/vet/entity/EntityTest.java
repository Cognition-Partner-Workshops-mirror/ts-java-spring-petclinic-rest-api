package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests covering entity constructors and getter/setter methods for coverage completeness.
 */
class EntityTest {

    @Test
    void specialty_defaultConstructor() {
        Specialty s = new Specialty();
        assertThat(s.getId()).isNull();
        assertThat(s.getName()).isNull();
        assertThat(s.getCreatedAt()).isNull();
        assertThat(s.getUpdatedAt()).isNull();
    }

    @Test
    void specialty_argConstructor_andSetters() {
        Specialty s = new Specialty(1, "radiology");
        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");

        LocalDateTime now = LocalDateTime.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void vet_defaultConstructor() {
        Vet v = new Vet();
        assertThat(v.getId()).isNull();
        assertThat(v.getFirstName()).isNull();
        assertThat(v.getLastName()).isNull();
        assertThat(v.getSpecialties()).isEmpty();
    }

    @Test
    void vet_argConstructor_andSetters() {
        Vet v = new Vet(1, "James", "Carter");
        assertThat(v.getId()).isEqualTo(1);
        assertThat(v.getFirstName()).isEqualTo("James");
        assertThat(v.getLastName()).isEqualTo("Carter");

        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));
        v.setSpecialties(specialties);
        assertThat(v.getSpecialties()).hasSize(1);

        LocalDateTime now = LocalDateTime.now();
        v.setCreatedAt(now);
        v.setUpdatedAt(now);
        assertThat(v.getCreatedAt()).isEqualTo(now);
        assertThat(v.getUpdatedAt()).isEqualTo(now);
    }
}
