package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void vet_gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        Set<Specialty> specs = new HashSet<>();
        vet.setSpecialties(specs);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).isSameAs(specs);
    }

    @Test
    void vet_constructorWithArgs() {
        Vet vet = new Vet(5, "Helen", "Leary");
        assertThat(vet.getId()).isEqualTo(5);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
    }

    @Test
    void specialty_gettersAndSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);
        Set<Vet> vets = new HashSet<>();
        s.setVets(vets);

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
        assertThat(s.getVets()).isSameAs(vets);
    }

    @Test
    void specialty_constructorWithArgs() {
        Specialty s = new Specialty(3, "surgery");
        assertThat(s.getId()).isEqualTo(3);
        assertThat(s.getName()).isEqualTo("surgery");
    }
}
