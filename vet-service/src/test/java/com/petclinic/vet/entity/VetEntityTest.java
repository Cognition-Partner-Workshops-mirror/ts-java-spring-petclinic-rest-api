package com.petclinic.vet.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityTest {

    @Test
    void defaultConstructor_createsEmptyVet() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
        assertThat(vet.getFirstName()).isNull();
        assertThat(vet.getLastName()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void parameterizedConstructor_setsNames() {
        Vet vet = new Vet("James", "Carter");
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");

        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        Set<Specialty> specs = new HashSet<>();
        specs.add(new Specialty("surgery"));
        vet.setSpecialties(specs);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void addSpecialty_addsToSet() {
        Vet vet = new Vet("James", "Carter");
        Specialty specialty = new Specialty("radiology");

        vet.addSpecialty(specialty);

        assertThat(vet.getSpecialties()).contains(specialty);
    }

    @Test
    void removeSpecialty_removesFromSet() {
        Vet vet = new Vet("James", "Carter");
        Specialty specialty = new Specialty("radiology");
        vet.addSpecialty(specialty);

        vet.removeSpecialty(specialty);

        assertThat(vet.getSpecialties()).doesNotContain(specialty);
    }
}
