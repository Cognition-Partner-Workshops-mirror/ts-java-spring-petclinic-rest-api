package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        entityManager.persist(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        entityManager.persist(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        entityManager.persist(dentistry);

        entityManager.flush();
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatchingSpecialties() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("SURG");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_partialMatch() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("ry");

        assertThat(result).hasSize(2);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllSpecialties() {
        List<Specialty> result = specialtyRepository.findAll();

        assertThat(result).hasSize(3);
    }
}
