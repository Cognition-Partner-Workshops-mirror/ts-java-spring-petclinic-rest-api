package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Vet and Specialty entity classes.
 * Covers constructors, getters/setters, audit callbacks, and convenience methods.
 */
class VetEntityTest {

    @Test
    void vet_defaultConstructor_createsEmptyVet() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
        assertThat(vet.getFirstName()).isNull();
        assertThat(vet.getLastName()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void vet_parameterizedConstructor_setsFields() {
        Vet vet = new Vet(1, "James", "Carter");
        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void vet_settersAndGetters_workCorrectly() {
        Vet vet = new Vet();
        vet.setId(5);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        assertThat(vet.getId()).isEqualTo(5);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
    }

    @Test
    void vet_addSpecialty_addsToSet() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty(1, "radiology");
        vet.addSpecialty(specialty);

        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(specialty);
    }

    @Test
    void vet_clearSpecialties_removesAll() {
        Vet vet = new Vet();
        vet.addSpecialty(new Specialty(1, "radiology"));
        vet.addSpecialty(new Specialty(2, "surgery"));
        assertThat(vet.getSpecialties()).hasSize(2);

        vet.clearSpecialties();
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void vet_setSpecialties_replacesSet() {
        Vet vet = new Vet();
        Set<Specialty> specialties = Set.of(new Specialty(1, "radiology"));
        vet.setSpecialties(specialties);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_onCreate_setsAuditTimestamps() {
        Vet vet = new Vet();
        vet.onCreate();
        assertThat(vet.getCreatedAt()).isNotNull();
        assertThat(vet.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_onUpdate_setsUpdatedAt() {
        Vet vet = new Vet();
        vet.onCreate();
        var created = vet.getCreatedAt();
        vet.onUpdate();
        assertThat(vet.getUpdatedAt()).isNotNull();
        // createdAt should remain the same after update
        assertThat(vet.getCreatedAt()).isEqualTo(created);
    }

    @Test
    void vet_auditFieldSetters_workCorrectly() {
        Vet vet = new Vet();
        vet.onCreate();
        var now = vet.getCreatedAt();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void specialty_defaultConstructor_createsEmptySpecialty() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
    }

    @Test
    void specialty_parameterizedConstructor_setsFields() {
        Specialty specialty = new Specialty(1, "radiology");
        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void specialty_settersAndGetters_workCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(3);
        specialty.setName("dentistry");
        assertThat(specialty.getId()).isEqualTo(3);
        assertThat(specialty.getName()).isEqualTo("dentistry");
    }

    @Test
    void specialty_onCreate_setsAuditTimestamps() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        assertThat(specialty.getCreatedAt()).isNotNull();
        assertThat(specialty.getUpdatedAt()).isNotNull();
    }

    @Test
    void specialty_onUpdate_setsUpdatedAt() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        var created = specialty.getCreatedAt();
        specialty.onUpdate();
        assertThat(specialty.getUpdatedAt()).isNotNull();
        assertThat(specialty.getCreatedAt()).isEqualTo(created);
    }

    @Test
    void specialty_auditFieldSetters_workCorrectly() {
        Specialty specialty = new Specialty();
        specialty.onCreate();
        var now = specialty.getCreatedAt();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }
}
