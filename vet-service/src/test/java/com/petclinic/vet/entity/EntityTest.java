package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void vet_onCreate_setsTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();
        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
        assertThat(vet.getCreatedAt()).isEqualTo(vet.getUpdatedAt());
    }

    @Test
    void vet_onUpdate_setsUpdatedAt() {
        Vet vet = new Vet();
        vet.onCreate();
        Instant original = vet.getUpdatedAt();
        vet.onUpdate();
        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(original);
    }

    @Test
    void vet_gettersSetters() {
        Vet vet = new Vet();
        vet.setId(10);
        vet.setFirstName("Alice");
        vet.setLastName("Smith");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        Specialty s = new Specialty();
        s.setId(1);
        vet.setSpecialties(Set.of(s));

        assertThat(vet.getId()).isEqualTo(10);
        assertThat(vet.getFirstName()).isEqualTo("Alice");
        assertThat(vet.getLastName()).isEqualTo("Smith");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void specialty_onCreate_setsTimestamps() {
        Specialty s = new Specialty();
        s.onCreate();
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }

    @Test
    void specialty_onUpdate_setsUpdatedAt() {
        Specialty s = new Specialty();
        s.onCreate();
        Instant original = s.getUpdatedAt();
        s.onUpdate();
        assertThat(s.getUpdatedAt()).isAfterOrEqualTo(original);
    }

    @Test
    void specialty_gettersSetters() {
        Specialty s = new Specialty();
        s.setId(5);
        s.setName("oncology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);

        assertThat(s.getId()).isEqualTo(5);
        assertThat(s.getName()).isEqualTo("oncology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
    }
}
