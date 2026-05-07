package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Vet entity getters/setters and constructors.
 */
class VetEntityTest {

    @Test
    void defaultConstructor_createsEmptyEntity() {
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
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));

        vet.setId(10);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setSpecialties(specialties);
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getId()).isEqualTo(10);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }
}
