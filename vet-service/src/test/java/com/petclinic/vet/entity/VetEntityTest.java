package com.petclinic.vet.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityTest {

    @Test
    void gettersAndSetters() {
        Vet v = new Vet();
        v.setId(1);
        v.setFirstName("James");
        v.setLastName("Carter");
        Instant now = Instant.now();
        v.setCreatedAt(now);
        v.setUpdatedAt(now);

        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s);
        v.setSpecialties(specialties);

        assertThat(v.getId()).isEqualTo(1);
        assertThat(v.getFirstName()).isEqualTo("James");
        assertThat(v.getLastName()).isEqualTo("Carter");
        assertThat(v.getCreatedAt()).isEqualTo(now);
        assertThat(v.getUpdatedAt()).isEqualTo(now);
        assertThat(v.getSpecialties()).hasSize(1);
    }

    @Test
    void onCreate_setsTimestamps() {
        Vet v = new Vet();
        v.onCreate();

        assertThat(v.getCreatedAt()).isNotNull();
        assertThat(v.getUpdatedAt()).isNotNull();
        assertThat(v.getCreatedAt()).isEqualTo(v.getUpdatedAt());
    }

    @Test
    void onUpdate_setsUpdatedAt() {
        Vet v = new Vet();
        v.onCreate();
        Instant original = v.getUpdatedAt();

        v.onUpdate();

        assertThat(v.getUpdatedAt()).isNotNull();
        assertThat(v.getCreatedAt()).isNotEqualTo(v.getUpdatedAt());
    }

    @Test
    void specialties_defaultsToEmptySet() {
        Vet v = new Vet();
        assertThat(v.getSpecialties()).isEmpty();
    }
}
