package com.petclinic.vet.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void defaultConstructor_createsEmptySpecialty() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
    }

    @Test
    void parameterizedConstructor_setsName() {
        Specialty specialty = new Specialty("radiology");
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("surgery");

        Instant now = Instant.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        Set<Vet> vets = new HashSet<>();
        vets.add(new Vet("James", "Carter"));
        specialty.setVets(vets);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
        assertThat(specialty.getVets()).hasSize(1);
    }
}
