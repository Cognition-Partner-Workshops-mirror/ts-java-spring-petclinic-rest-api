package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link SpecialtyRepository} using @DataJpaTest with H2.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        Specialty saved = specialtyRepository.save(specialty);

        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");
        specialtyRepository.save(specialty);

        Optional<Specialty> found = specialtyRepository.findByNameIgnoreCase("surgery");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Surgery");
    }

    @Test
    void shouldReturnEmptyWhenNameNotFound() {
        Optional<Specialty> found = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setName("cardiology");
        Specialty s3 = new Specialty();
        s3.setName("surgery");
        specialtyRepository.saveAll(List.of(s1, s2, s3));

        // Both contain "ology"
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("ology");
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Specialty::getName).containsExactlyInAnyOrder("radiology", "cardiology");
    }

    @Test
    void shouldFindAll() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setName("surgery");
        specialtyRepository.saveAll(List.of(s1, s2));

        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("dentistry");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.deleteById(saved.getId());
        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldUpdateSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("old name");
        Specialty saved = specialtyRepository.save(specialty);

        saved.setName("new name");
        specialtyRepository.save(saved);

        Optional<Specialty> updated = specialtyRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("new name");
    }
}
