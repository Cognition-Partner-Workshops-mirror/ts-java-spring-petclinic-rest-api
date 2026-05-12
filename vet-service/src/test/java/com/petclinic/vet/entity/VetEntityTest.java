package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Vet entity getters, setters, and default behavior.
 */
class VetEntityTest {

    @Test
    void defaultConstructor_setsDefaults() {
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

        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
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
}
