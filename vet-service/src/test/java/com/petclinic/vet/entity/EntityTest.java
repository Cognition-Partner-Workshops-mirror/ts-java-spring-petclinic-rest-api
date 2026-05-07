package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_gettersSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);
        s.setVets(new HashSet<>());

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
        assertThat(s.getVets()).isEmpty();
    }

    @Test
    void specialty_prePersist() {
        Specialty s = new Specialty();
        s.onCreate();
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }

    @Test
    void specialty_preUpdate() {
        Specialty s = new Specialty();
        s.onCreate();
        Instant before = s.getUpdatedAt();
        s.onUpdate();
        assertThat(s.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void vet_gettersSetters() {
        Vet v = new Vet();
        v.setId(1);
        v.setFirstName("James");
        v.setLastName("Carter");
        Instant now = Instant.now();
        v.setCreatedAt(now);
        v.setUpdatedAt(now);
        v.setSpecialties(new HashSet<>());

        assertThat(v.getId()).isEqualTo(1);
        assertThat(v.getFirstName()).isEqualTo("James");
        assertThat(v.getLastName()).isEqualTo("Carter");
        assertThat(v.getCreatedAt()).isEqualTo(now);
        assertThat(v.getUpdatedAt()).isEqualTo(now);
        assertThat(v.getSpecialties()).isEmpty();
    }

    @Test
    void vet_prePersist() {
        Vet v = new Vet();
        v.onCreate();
        assertThat(v.getCreatedAt()).isNotNull();
        assertThat(v.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_preUpdate() {
        Vet v = new Vet();
        v.onCreate();
        Instant before = v.getUpdatedAt();
        v.onUpdate();
        assertThat(v.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void vet_specialtiesRelationship() {
        Vet v = new Vet();
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        Set<Specialty> specs = new HashSet<>();
        specs.add(s1);
        specs.add(s2);
        v.setSpecialties(specs);

        assertThat(v.getSpecialties()).hasSize(2);
    }
}
