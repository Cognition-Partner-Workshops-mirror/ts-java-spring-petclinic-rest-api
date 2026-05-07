package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void gettersAndSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void onCreate_setsTimestamps() {
        Specialty s = new Specialty();
        s.onCreate();
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_setsUpdatedAt() {
        Specialty s = new Specialty();
        s.onCreate();
        s.onUpdate();
        assertThat(s.getUpdatedAt()).isNotNull();
    }
}
