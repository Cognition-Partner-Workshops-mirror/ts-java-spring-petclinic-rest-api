package com.petclinic.vet.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityTest {

    @Test
    void gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void prePersist_setsTimestamps() {
        Vet vet = new Vet();
        vet.prePersist();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
        assertThat(vet.getCreatedAt()).isEqualTo(vet.getUpdatedAt());
    }

    @Test
    void preUpdate_updatesTimestamp() {
        Vet vet = new Vet();
        vet.prePersist();
        Instant original = vet.getUpdatedAt();

        vet.preUpdate();

        assertThat(vet.getUpdatedAt()).isAfterOrEqualTo(original);
    }
}
