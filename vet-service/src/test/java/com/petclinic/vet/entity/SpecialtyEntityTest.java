package com.petclinic.vet.entity;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void gettersAndSetters() {
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
    void prePersist_setsTimestamps() {
        Specialty specialty = new Specialty();
        specialty.prePersist();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void preUpdate_updatesTimestamp() {
        Specialty specialty = new Specialty();
        specialty.prePersist();
        Instant original = specialty.getUpdatedAt();

        specialty.preUpdate();

        assertThat(specialty.getUpdatedAt()).isAfterOrEqualTo(original);
    }
}
