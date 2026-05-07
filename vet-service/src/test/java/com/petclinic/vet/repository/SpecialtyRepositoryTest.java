package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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
    void findAll_returnsAllSpecialties() {
        List<Specialty> all = repository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatch() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("dentistry");
        assertThat(results).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_true() {
        assertThat(repository.existsByNameIgnoreCase("Radiology")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_false() {
        assertThat(repository.existsByNameIgnoreCase("dentistry")).isFalse();
    }

    @Test
    void save_setsAuditFields() {
        Specialty s = new Specialty();
        s.setName("dentistry");
        Specialty saved = repository.save(s);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
