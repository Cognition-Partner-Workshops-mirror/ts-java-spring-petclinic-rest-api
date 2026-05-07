package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = repository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = repository.save(surgery);
    }

    @Test
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("RADIOLOGY");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("dentistry");

        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_partialMatch_returnsResults() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_existingName_returnsTrue() {
        assertThat(repository.existsByNameIgnoreCase("surgery")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_nonExistingName_returnsFalse() {
        assertThat(repository.existsByNameIgnoreCase("dentistry")).isFalse();
    }

    @Test
    void save_newSpecialty_setsAuditFields() {
        Specialty specialty = new Specialty();
        specialty.setName("dentistry");
        Specialty saved = repository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
