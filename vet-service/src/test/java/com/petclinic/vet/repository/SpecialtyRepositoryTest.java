package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
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

    @Test
    void findAll_returnsSeededData() {
        List<Specialty> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> found = repository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound() {
        Optional<Specialty> found = repository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty s = new Specialty();
        s.setName("oncology");
        Specialty saved = repository.save(s);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatches() {
        List<Specialty> found = repository.findByNameContainingIgnoreCase("rad");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> found = repository.findByNameContainingIgnoreCase("xyz");
        assertThat(found).isEmpty();
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
        Specialty updated = repository.save(s);
        assertThat(updated.getName()).isEqualTo("updated-radiology");
    }
}
