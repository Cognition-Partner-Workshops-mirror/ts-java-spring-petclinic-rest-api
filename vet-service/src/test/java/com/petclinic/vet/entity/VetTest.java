package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void onCreate_setsAuditFields() {
        Vet vet = new Vet();
        vet.onCreate();

        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
        assertThat(vet.getCreatedAt()).isEqualTo(vet.getUpdatedAt());
    }

    @Test
    void onUpdate_updatesTimestamp() throws InterruptedException {
        Vet vet = new Vet();
        vet.onCreate();
        var created = vet.getUpdatedAt();
        Thread.sleep(10);
        vet.onUpdate();

        assertThat(vet.getUpdatedAt()).isAfter(created);
        assertThat(vet.getCreatedAt()).isEqualTo(created.equals(vet.getCreatedAt()) ? vet.getCreatedAt() : null);
    }

    @Test
    void gettersAndSetters_workCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Set<Specialty> specs = new HashSet<>();
        vet.setSpecialties(specs);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).isSameAs(specs);
    }
}
