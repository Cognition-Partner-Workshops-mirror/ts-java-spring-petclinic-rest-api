package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).isNotEmpty();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsAndGeneratesId() {
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("oncology");

        Specialty saved = specialtyRepository.save(newSpecialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");

        Optional<Specialty> fetched = specialtyRepository.findById(saved.getId());
        assertThat(fetched).isPresent();
    }

    @Test
    void delete_existingSpecialty_removesFromDb() {
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("temp-specialty");
        Specialty saved = specialtyRepository.save(newSpecialty);

        specialtyRepository.delete(saved);

        Optional<Specialty> fetched = specialtyRepository.findById(saved.getId());
        assertThat(fetched).isEmpty();
    }

    @Test
    void findByNameIn_returnsMatchingSpecialties() {
        List<Specialty> results = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Specialty::getName)
            .containsExactlyInAnyOrder("radiology", "surgery");
    }

    @Test
    void findByName_existingName_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findByName("radiology");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByName_nonExistingName_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findByName("nonexistent");
        assertThat(result).isEmpty();
    }
}
