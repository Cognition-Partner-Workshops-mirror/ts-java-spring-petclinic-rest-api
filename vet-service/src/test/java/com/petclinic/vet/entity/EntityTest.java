package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_gettersAndSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void specialty_constructorWithArgs() {
        Specialty s = new Specialty(2, "surgery");
        assertThat(s.getId()).isEqualTo(2);
        assertThat(s.getName()).isEqualTo("surgery");
    }

    @Test
    void vet_gettersAndSetters() {
        Vet v = new Vet();
        v.setId(1);
        v.setFirstName("James");
        v.setLastName("Carter");
        Instant now = Instant.now();
        v.setCreatedAt(now);
        v.setUpdatedAt(now);
        Set<Specialty> specs = new HashSet<>();
        specs.add(new Specialty(1, "radiology"));
        v.setSpecialties(specs);

        assertThat(v.getId()).isEqualTo(1);
        assertThat(v.getFirstName()).isEqualTo("James");
        assertThat(v.getLastName()).isEqualTo("Carter");
        assertThat(v.getCreatedAt()).isEqualTo(now);
        assertThat(v.getUpdatedAt()).isEqualTo(now);
        assertThat(v.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_constructorWithArgs() {
        Vet v = new Vet(3, "Helen", "Leary");
        assertThat(v.getId()).isEqualTo(3);
        assertThat(v.getFirstName()).isEqualTo("Helen");
        assertThat(v.getLastName()).isEqualTo("Leary");
    }
}
