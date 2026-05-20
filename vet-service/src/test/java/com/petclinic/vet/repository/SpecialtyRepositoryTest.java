package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.config.JpaAuditConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SpecialtyRepository using @DataJpaTest.
 * Tests custom query methods for name-based searching.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();

        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        specialtyRepository.save(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        specialtyRepository.save(dentistry);
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSize(3);
    }

    @Test
    void findByNameContainingIgnoreCase_shouldFindMatchingSpecialties() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldBeCaseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnEmptyForNoMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameInIgnoreCase_shouldFindMultipleByName() {
        List<Specialty> results = specialtyRepository.findByNameInIgnoreCase(Set.of("radiology", "surgery"));
        assertThat(results).hasSize(2);
    }

    @Test
    void findByNameInIgnoreCase_shouldReturnEmptyForUnknownNames() {
        List<Specialty> results = specialtyRepository.findByNameInIgnoreCase(Set.of("unknown"));
        assertThat(results).isEmpty();
    }

    @Test
    void save_shouldPersistNewSpecialty() {
        Specialty cardiology = new Specialty();
        cardiology.setName("cardiology");
        Specialty saved = specialtyRepository.save(cardiology);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void deleteById_shouldRemoveSpecialty() {
        Specialty specialty = specialtyRepository.findAll().get(0);
        specialtyRepository.deleteById(specialty.getId());
        assertThat(specialtyRepository.findById(specialty.getId())).isEmpty();
    }
}
