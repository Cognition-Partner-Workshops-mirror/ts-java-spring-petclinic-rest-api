package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
        Set<Specialty> specs = new HashSet<>();
        v.setSpecialties(specs);

        assertThat(v.getId()).isEqualTo(1);
        assertThat(v.getFirstName()).isEqualTo("James");
        assertThat(v.getLastName()).isEqualTo("Carter");
        assertThat(v.getCreatedAt()).isEqualTo(now);
        assertThat(v.getUpdatedAt()).isEqualTo(now);
        assertThat(v.getSpecialties()).isSameAs(specs);
    }

    @Test
    void prePersistSetsTimestamps() {
        Vet v = new Vet();
        v.onCreate();
        assertThat(v.getCreatedAt()).isNotNull();
        assertThat(v.getUpdatedAt()).isNotNull();
    }

    @Test
    void preUpdateSetsUpdatedAt() {
        Vet v = new Vet();
        v.onCreate();
        v.onUpdate();
        assertThat(v.getUpdatedAt()).isNotNull();
    }
}
