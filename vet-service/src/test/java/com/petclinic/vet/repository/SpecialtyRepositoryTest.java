package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameContainingIgnoreCase_returnsMatchingSpecialties() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("RAD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsSeedData() {
        List<Specialty> result = specialtyRepository.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void saveAndRetrieve() {
        Specialty oncology = new Specialty("oncology");
        specialtyRepository.flush();
        Specialty saved = specialtyRepository.saveAndFlush(oncology);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(3);
        assertThat(saved.getName()).isEqualTo("oncology");

        Specialty found = specialtyRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("oncology");
    }
}
