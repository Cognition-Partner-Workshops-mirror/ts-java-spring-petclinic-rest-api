package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

// Unit tests for Vet entity getters, setters, and specialty management
class VetEntityTest {

    @Test
    void defaultConstructor_initializesEmptySpecialties() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
        assertThat(vet.getFirstName()).isNull();
        assertThat(vet.getLastName()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void parameterizedConstructor_setsFields() {
        Vet vet = new Vet("James", "Carter");
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void auditFields_canBeSetAndRead() {
        Vet vet = new Vet("James", "Carter");
        LocalDateTime now = LocalDateTime.now();

        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void addSpecialty_addsToSet() {
        Vet vet = new Vet("James", "Carter");
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);

        vet.addSpecialty(radiology);

        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(radiology);
    }

    @Test
    void removeSpecialty_removesFromSet() {
        Vet vet = new Vet("James", "Carter");
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);
        vet.addSpecialty(radiology);

        vet.removeSpecialty(radiology);

        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void setSpecialties_replacesExistingSet() {
        Vet vet = new Vet("James", "Carter");
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(radiology);

        vet.setSpecialties(specialties);

        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(radiology);
    }
}
