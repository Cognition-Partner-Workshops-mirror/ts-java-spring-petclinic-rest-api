package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EntityAuditTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private VetRepository vetRepository;

    @Test
    void specialtyAuditFieldsSetOnCreate() {
        Specialty specialty = new Specialty();
        specialty.setName("ophthalmology");
        Specialty saved = entityManager.persistFlushFind(specialty);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void specialtyAuditFieldsUpdatedOnModify() throws InterruptedException {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = entityManager.persistFlushFind(specialty);
        var originalUpdatedAt = saved.getUpdatedAt();

        Thread.sleep(10);

        saved.setName("cardiology-updated");
        entityManager.flush();
        entityManager.clear();

        Specialty updated = specialtyRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void vetAuditFieldsSetOnCreate() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        Vet saved = entityManager.persistFlushFind(vet);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void vetAuditFieldsUpdatedOnModify() throws InterruptedException {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        Vet saved = entityManager.persistFlushFind(vet);
        var originalUpdatedAt = saved.getUpdatedAt();

        Thread.sleep(10);

        saved.setLastName("Updated");
        entityManager.flush();
        entityManager.clear();

        Vet updated = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void vetSpecialtyRelationship() {
        Specialty spec = specialtyRepository.findByNameIgnoreCase("radiology").orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>(Set.of(spec)));
        Vet saved = entityManager.persistFlushFind(vet);

        assertThat(saved.getSpecialties()).hasSize(1);
        assertThat(saved.getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyGettersAndSetters() {
        Specialty specialty = new Specialty();
        specialty.setId(42);
        specialty.setName("test");

        assertThat(specialty.getId()).isEqualTo(42);
        assertThat(specialty.getName()).isEqualTo("test");
    }

    @Test
    void vetGettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(42);
        vet.setFirstName("First");
        vet.setLastName("Last");
        vet.setSpecialties(new HashSet<>());

        assertThat(vet.getId()).isEqualTo(42);
        assertThat(vet.getFirstName()).isEqualTo("First");
        assertThat(vet.getLastName()).isEqualTo("Last");
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
