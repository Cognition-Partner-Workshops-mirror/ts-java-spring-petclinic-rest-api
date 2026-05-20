package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Specialty entity to verify getters and setters.
 */
class SpecialtyEntityTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
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
    void vets_shouldBeSettableAndGettable() {
        Specialty specialty = new Specialty();
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Set<Vet> vets = new HashSet<>();
        vets.add(vet);
        specialty.setVets(vets);

        assertThat(specialty.getVets()).hasSize(1);
    }
}
