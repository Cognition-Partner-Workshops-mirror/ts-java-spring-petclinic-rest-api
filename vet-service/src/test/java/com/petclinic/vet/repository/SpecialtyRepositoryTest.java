package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
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
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> found = repository.findById(radiology.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> found = repository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_matchingName_returnsResults() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("SURG");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("dentistry");
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameIgnoreCase_exactMatch() {
        List<Specialty> results = repository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void save_newSpecialty_assignsId() {
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        Specialty saved = repository.save(dentistry);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("dentistry");
    }

    @Test
    void delete_removesSpecialty() {
        repository.delete(radiology);
        Optional<Specialty> found = repository.findById(radiology.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_changesName() {
        radiology.setName("updated-radiology");
        repository.save(radiology);
        Optional<Specialty> found = repository.findById(radiology.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("updated-radiology");
    }
}
