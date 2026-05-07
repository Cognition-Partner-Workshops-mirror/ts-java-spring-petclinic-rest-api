package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.petclinic.vet.config.JpaAuditingConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existing_returnsSpecialty() {
        assertThat(specialtyRepository.findById(1)).isPresent();
    }

    @Test
    void findById_nonExisting_returnsEmpty() {
        assertThat(specialtyRepository.findById(999)).isEmpty();
    }

    @Test
    void findByNameIn_returnsMatches() {
        List<Specialty> found = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(found).hasSize(2);
    }

    @Test
    void findByNameIn_noMatch_returnsEmpty() {
        List<Specialty> found = specialtyRepository.findByNameIn(Set.of("nonexistent"));
        assertThat(found).isEmpty();
    }

    @Test
    void existsByName_existing_returnsTrue() {
        assertThat(specialtyRepository.existsByName("radiology")).isTrue();
    }

    @Test
    void existsByName_nonExisting_returnsFalse() {
        assertThat(specialtyRepository.existsByName("nonexistent")).isFalse();
    }

    @Test
    void saveAndDelete_specialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();

        specialtyRepository.delete(saved);
        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }
}
