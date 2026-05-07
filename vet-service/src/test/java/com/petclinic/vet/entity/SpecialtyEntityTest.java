package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyEntityTest {

    @Test
    void gettersAndSetters() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("surgery");
        Instant now = Instant.now();
        s.setCreatedAt(now);
        s.setUpdatedAt(now);

        assertThat(s.getId()).isEqualTo(1);
        assertThat(s.getName()).isEqualTo("surgery");
        assertThat(s.getCreatedAt()).isEqualTo(now);
        assertThat(s.getUpdatedAt()).isEqualTo(now);
    }
}
