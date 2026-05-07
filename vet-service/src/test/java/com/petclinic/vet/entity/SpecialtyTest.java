package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void onCreate_setsAuditFields() {
        Specialty specialty = new Specialty();
        specialty.onCreate();

        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_updatesTimestamp() throws InterruptedException {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        var original = specialty.getUpdatedAt();
        Thread.sleep(10);
        specialty.onUpdate();

        assertThat(specialty.getUpdatedAt()).isAfter(original);
    }

    @Test
    void gettersAndSetters_workCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }
}
