package com.petclinic.vet.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EntityAuditTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void specialty_auditFieldsPopulatedOnPersist() {
        Specialty specialty = new Specialty();
        specialty.setName("neurology");
        Specialty saved = entityManager.persistFlushFind(specialty);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void vet_auditFieldsPopulatedOnPersist() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        vet.setSpecialties(new HashSet<>());
        Vet saved = entityManager.persistFlushFind(vet);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getSpecialties()).isEmpty();
    }

    @Test
    void vet_manyToManyRelationship() {
        Specialty s1 = new Specialty();
        s1.setName("cardiology");
        s1 = entityManager.persistAndFlush(s1);

        Specialty s2 = new Specialty();
        s2.setName("dermatology");
        s2 = entityManager.persistAndFlush(s2);

        Vet vet = new Vet();
        vet.setFirstName("Jane");
        vet.setLastName("Smith");
        vet.setSpecialties(new HashSet<>(Set.of(s1, s2)));
        Vet saved = entityManager.persistFlushFind(vet);

        assertThat(saved.getSpecialties()).hasSize(2);
        assertThat(saved.getSpecialties()).extracting(Specialty::getName)
            .containsExactlyInAnyOrder("cardiology", "dermatology");
    }
}
