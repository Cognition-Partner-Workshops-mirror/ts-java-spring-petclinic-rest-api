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
        List<Specialty> result = repository.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> result = repository.findById(radiology.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> result = repository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_matchesPartialName() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("radio");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("dentistry");
        assertThat(result).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        Specialty saved = repository.save(dentistry);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("dentistry");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void delete_removesSpecialty() {
        repository.delete(radiology);
        assertThat(repository.findById(radiology.getId())).isEmpty();
    }
}
