package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> found = repository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> found = repository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsSuccessfully() {
        Specialty s = new Specialty();
        s.setName("oncology");
        Specialty saved = repository.save(s);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatching() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("surg");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("RADIO");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void delete_removesSpecialty() {
        Specialty s = new Specialty();
        s.setName("temp");
        Specialty saved = repository.save(s);
        repository.delete(saved);
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void update_modifiesSpecialty() {
        Specialty s = repository.findById(1).orElseThrow();
        s.setName("updated-radiology");
        repository.save(s);
        Specialty updated = repository.findById(1).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated-radiology");
    }
}
