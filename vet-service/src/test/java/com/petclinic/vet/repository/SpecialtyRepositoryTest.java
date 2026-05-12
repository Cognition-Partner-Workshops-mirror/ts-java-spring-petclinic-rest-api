package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link SpecialtyRepository} using an embedded H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveSpecialty() {
        Specialty specialty = new Specialty("radiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        specialtyRepository.save(new Specialty("Surgery"));

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
        specialtyRepository.save(new Specialty("radiology"));
        specialtyRepository.save(new Specialty("cardiology"));
        specialtyRepository.save(new Specialty("surgery"));

        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("ology");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Specialty::getName)
                .containsExactlyInAnyOrder("radiology", "cardiology");
    }

    @Test
    void shouldReturnEmptyListWhenNoNameMatch() {
        specialtyRepository.save(new Specialty("surgery"));

        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void shouldCheckExistsByNameIgnoreCase() {
        specialtyRepository.save(new Specialty("dentistry"));

        assertThat(specialtyRepository.existsByNameIgnoreCase("DENTISTRY")).isTrue();
        assertThat(specialtyRepository.existsByNameIgnoreCase("radiology")).isFalse();
    }

    @Test
    void shouldFindAllSpecialties() {
        specialtyRepository.save(new Specialty("radiology"));
        specialtyRepository.save(new Specialty("surgery"));
        specialtyRepository.save(new Specialty("dentistry"));

        List<Specialty> all = specialtyRepository.findAll();

        assertThat(all).hasSize(3);
    }

    @Test
    void shouldDeleteSpecialtyById() {
        Specialty saved = specialtyRepository.save(new Specialty("radiology"));
        Integer id = saved.getId();

        specialtyRepository.deleteById(id);

        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
