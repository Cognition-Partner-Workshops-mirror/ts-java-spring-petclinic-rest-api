package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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
    void findByNameContainingIgnoreCase_shouldReturnMatchingSpecialties() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldBeCaseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnEmptyForNoMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_shouldReturnTrueForExistingName() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("Radiology")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_shouldReturnFalseForNonExistingName() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("cardiology")).isFalse();
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        List<Specialty> results = specialtyRepository.findAll();
        assertThat(results).hasSize(3);
    }

    @Test
    void save_shouldPersistSpecialty() {
        Specialty neurology = new Specialty();
        neurology.setName("neurology");
        Specialty saved = specialtyRepository.save(neurology);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("neurology");
    }

    @Test
    void delete_shouldRemoveSpecialty() {
        List<Specialty> all = specialtyRepository.findAll();
        specialtyRepository.delete(all.get(0));
        assertThat(specialtyRepository.findAll()).hasSize(2);
    }
}
