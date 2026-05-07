package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    void specialty_parameterizedConstructor() {
        Specialty s = new Specialty(5, "surgery");
        assertThat(s.getId()).isEqualTo(5);
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
        List<Specialty> specs = new ArrayList<>();
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
    void vet_parameterizedConstructor() {
        List<Specialty> specs = List.of(new Specialty(1, "radiology"));
        Vet v = new Vet(2, "Helen", "Leary", specs);
        assertThat(v.getId()).isEqualTo(2);
        assertThat(v.getFirstName()).isEqualTo("Helen");
        assertThat(v.getLastName()).isEqualTo("Leary");
        assertThat(v.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_parameterizedConstructor_nullSpecialties() {
        Vet v = new Vet(3, "Solo", "Vet", null);
        assertThat(v.getSpecialties()).isEmpty();
    }
}
