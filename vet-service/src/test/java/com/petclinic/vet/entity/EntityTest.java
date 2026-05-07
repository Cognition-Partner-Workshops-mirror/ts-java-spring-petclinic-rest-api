package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

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
    void vet_gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void vet_specialties_defaultEmpty() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void vet_addSpecialty() {
        Vet vet = new Vet();
        Specialty spec = new Specialty();
        spec.setId(1);
        spec.setName("surgery");
        vet.addSpecialty(spec);
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(spec);
    }

    @Test
    void vet_setSpecialties() {
        Vet vet = new Vet();
        Specialty spec1 = new Specialty();
        spec1.setId(1);
        spec1.setName("surgery");
        Specialty spec2 = new Specialty();
        spec2.setId(2);
        spec2.setName("radiology");
        Set<Specialty> specs = new HashSet<>();
        specs.add(spec1);
        specs.add(spec2);
        vet.setSpecialties(specs);
        assertThat(vet.getSpecialties()).hasSize(2);
    }

    @Test
    void vet_clearSpecialties() {
        Vet vet = new Vet();
        Specialty spec = new Specialty();
        spec.setId(1);
        spec.setName("surgery");
        vet.addSpecialty(spec);
        assertThat(vet.getSpecialties()).hasSize(1);
        vet.clearSpecialties();
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
