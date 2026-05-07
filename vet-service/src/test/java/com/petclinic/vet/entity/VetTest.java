package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void defaultConstructor_createsEmptyVet() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
        assertThat(vet.getFirstName()).isNull();
        assertThat(vet.getLastName()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void parameterizedConstructor_setsFields() {
        Vet vet = new Vet(1, "James", "Carter");
        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Vet vet = new Vet();
        Instant now = Instant.now();

        vet.setId(5);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        vet.setSpecialties(Set.of(new Specialty(1, "radiology")));

        assertThat(vet.getId()).isEqualTo(5);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }
}
