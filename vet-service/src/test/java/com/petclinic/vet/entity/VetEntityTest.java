package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Vet entity to verify getters, setters, and sorting logic.
 */
class VetEntityTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
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
    void specialties_shouldBeSettableAndGettable() {
        Vet vet = new Vet();
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s1);
        specialties.add(s2);
        vet.setSpecialties(specialties);

        assertThat(vet.getSpecialties()).hasSize(2);
    }

    @Test
    void getSortedSpecialties_shouldReturnAlphabeticallySorted() {
        Vet vet = new Vet();

        Specialty surgery = new Specialty();
        surgery.setId(1);
        surgery.setName("surgery");

        Specialty dentistry = new Specialty();
        dentistry.setId(2);
        dentistry.setName("dentistry");

        Specialty radiology = new Specialty();
        radiology.setId(3);
        radiology.setName("radiology");

        vet.setSpecialties(Set.of(surgery, dentistry, radiology));

        List<Specialty> sorted = vet.getSortedSpecialties();

        assertThat(sorted).hasSize(3);
        assertThat(sorted.get(0).getName()).isEqualTo("dentistry");
        assertThat(sorted.get(1).getName()).isEqualTo("radiology");
        assertThat(sorted.get(2).getName()).isEqualTo("surgery");
    }

    @Test
    void getSortedSpecialties_emptySpecialties_shouldReturnEmptyList() {
        Vet vet = new Vet();
        vet.setSpecialties(new HashSet<>());

        assertThat(vet.getSortedSpecialties()).isEmpty();
    }
}
