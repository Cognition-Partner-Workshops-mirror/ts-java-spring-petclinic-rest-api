package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void gettersAndSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);
        Set<Vet> vets = new HashSet<>();
        s.setVets(vets);

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("radiology");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
        assertThat(s.getVets()).isSameAs(vets);
    }

    @Test
    void prePersistSetsTimestamps() {
        Specialty s = new Specialty();
        s.onCreate();
        assertThat(s.getCreatedAt()).isNotNull();
        assertThat(s.getUpdatedAt()).isNotNull();
    }

    @Test
    void preUpdateSetsUpdatedAt() {
        Specialty s = new Specialty();
        s.onCreate();
        Instant original = s.getUpdatedAt();
        s.onUpdate();
        assertThat(s.getUpdatedAt()).isNotNull();
    }
}
