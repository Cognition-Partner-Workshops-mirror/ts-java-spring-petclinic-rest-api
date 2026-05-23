package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for SpecialtyRepository using @DataJpaTest with H2.
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
    void save_and_findById() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        Specialty saved = specialtyRepository.save(specialty);

        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        specialtyRepository.save(s2);

        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findByNameIn_returnsMatches() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        specialtyRepository.save(s2);

        Specialty s3 = new Specialty();
        s3.setName("dentistry");
        specialtyRepository.save(s3);

        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(result).hasSize(2);
    }

    @Test
    void findByNameIn_noMatches() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        specialtyRepository.save(s1);

        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("unknown"));
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameIgnoreCase_existingName() {
        Specialty s = new Specialty();
        s.setName("radiology");
        specialtyRepository.save(s);

        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("unknown");
        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesSpecialty() {
        Specialty s = new Specialty();
        s.setName("radiology");
        s = specialtyRepository.save(s);
        specialtyRepository.delete(s);

        assertThat(specialtyRepository.findById(s.getId())).isEmpty();
    }

    @Test
    void update_modifiesName() {
        Specialty s = new Specialty();
        s.setName("radiology");
        s = specialtyRepository.save(s);

        s.setName("oncology");
        Specialty updated = specialtyRepository.save(s);
        assertThat(updated.getName()).isEqualTo("oncology");
    }
}
