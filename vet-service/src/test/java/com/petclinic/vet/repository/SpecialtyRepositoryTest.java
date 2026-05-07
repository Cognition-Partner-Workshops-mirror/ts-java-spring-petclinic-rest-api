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
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialtyRepository.save(specialty);

        Optional<Specialty> found = specialtyRepository.findByNameIgnoreCase("Radiology");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        Optional<Specialty> found = specialtyRepository.findByNameIgnoreCase("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void searchByName_partialMatch_returnsResults() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        specialtyRepository.save(s2);

        List<Specialty> results = specialtyRepository.searchByName("rad");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_caseInsensitive_returnsResults() {
        Specialty s1 = new Specialty();
        s1.setName("Radiology");
        specialtyRepository.save(s1);

        List<Specialty> results = specialtyRepository.searchByName("radiology");

        assertThat(results).hasSize(1);
    }

    @Test
    void findByIdIn_returnsMatchingSpecialties() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        s1 = specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        s2 = specialtyRepository.save(s2);

        Specialty s3 = new Specialty();
        s3.setName("dentistry");
        specialtyRepository.save(s3);

        List<Specialty> results = specialtyRepository.findByIdIn(
            List.of(s1.getId(), s2.getId()));

        assertThat(results).hasSize(2);
    }

    @Test
    void save_setsAuditFields() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");

        Specialty saved = specialtyRepository.save(specialty);
        specialtyRepository.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
