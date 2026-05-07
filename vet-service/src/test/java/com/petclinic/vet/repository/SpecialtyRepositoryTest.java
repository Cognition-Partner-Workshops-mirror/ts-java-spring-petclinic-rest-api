package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
        entityManager.clear();
    }

    @Test
    void findByNameIn_shouldReturnMatchingSpecialties() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(specialties).hasSize(2);
    }

    @Test
    void findByNameIn_shouldReturnEmptyForNoMatch() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("unknown"));
        assertThat(specialties).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatching() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(specialties).hasSize(1);
        assertThat(specialties.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void save_shouldPersistNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("ophthalmology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void delete_shouldRemoveSpecialty() {
        List<Specialty> before = specialtyRepository.findAll();
        specialtyRepository.delete(before.get(0));
        entityManager.flush();
        List<Specialty> after = specialtyRepository.findAll();
        assertThat(after).hasSize(before.size() - 1);
    }
}
