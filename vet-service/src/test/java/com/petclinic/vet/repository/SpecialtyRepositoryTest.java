package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        repository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        repository.save(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        repository.save(dentistry);
    }

    @Test
    void findAll_returnsAllSpecialties() {
        List<Specialty> result = repository.findAll();
        assertThat(result).hasSize(3);
    }

    @Test
    void findByNameContainingIgnoreCase_matchesPartialName() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("RAD");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("cardiology");
        assertThat(result).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        Specialty s = new Specialty();
        s.setName("oncology");
        Specialty saved = repository.save(s);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
