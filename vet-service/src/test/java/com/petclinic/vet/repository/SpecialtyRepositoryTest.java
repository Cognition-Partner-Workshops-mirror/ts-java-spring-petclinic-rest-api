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
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_matchingName_returnsResults() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive_returnsResults() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("SURG");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void save_newSpecialty_persists() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_existingSpecialty_removes() {
        Specialty specialty = new Specialty();
        specialty.setName("temp");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.delete(saved);
        Optional<Specialty> result = specialtyRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
