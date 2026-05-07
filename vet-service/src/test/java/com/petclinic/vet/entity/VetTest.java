package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void gettersAndSetters() {
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
    void specialtiesDefaultsToEmptySet() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isNotNull().isEmpty();
    }

    @Test
    void setSpecialties() {
        Vet vet = new Vet();
        Set<Specialty> specs = new HashSet<>();
        Specialty s = new Specialty();
        s.setName("radiology");
        specs.add(s);
        vet.setSpecialties(specs);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void addSpecialty() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setName("surgery");
        vet.addSpecialty(s);
        assertThat(vet.getSpecialties()).hasSize(1).contains(s);
    }

    @Test
    void clearSpecialties() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setName("dentistry");
        vet.addSpecialty(s);
        vet.clearSpecialties();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void getSpecialtiesSorted_returnsSortedByName() {
        Vet vet = new Vet();
        Specialty s1 = new Specialty();
        s1.setName("surgery");
        Specialty s2 = new Specialty();
        s2.setName("dentistry");
        Specialty s3 = new Specialty();
        s3.setName("radiology");
        vet.addSpecialty(s1);
        vet.addSpecialty(s2);
        vet.addSpecialty(s3);

        List<Specialty> sorted = vet.getSpecialtiesSorted();
        assertThat(sorted).extracting(Specialty::getName)
            .containsExactly("dentistry", "radiology", "surgery");
    }

    @Test
    void onCreate_setsTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();
        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_setsUpdatedAt() {
        Vet vet = new Vet();
        vet.onCreate();
        Instant original = vet.getUpdatedAt();
        vet.onUpdate();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }
}
