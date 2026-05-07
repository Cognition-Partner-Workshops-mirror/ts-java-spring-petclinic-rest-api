package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityTest {

    @Test
    void gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));
        vet.setSpecialties(specialties);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void constructorWithArgs() {
        Vet vet = new Vet(3, "Helen", "Leary");
        assertThat(vet.getId()).isEqualTo(3);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
    }

    @Test
    void defaultSpecialtiesIsEmpty() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
