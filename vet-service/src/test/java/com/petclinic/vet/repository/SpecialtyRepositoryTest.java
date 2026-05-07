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
    void findAll_returnsSeededSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
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
    void save_newSpecialty_assignsId() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void findByNameContainingIgnoreCase_matchesPartialName() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("SURG");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameIn_returnsMatchingSpecialties() {
        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(result).hasSize(2);
    }

    @Test
    void findByNameIn_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("nonexistent"));
        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temp");
        specialty = specialtyRepository.save(specialty);
        Integer id = specialty.getId();

        specialtyRepository.delete(specialty);
        assertThat(specialtyRepository.findById(id)).isEmpty();
    }

    @Test
    void update_changesName() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        specialty.setName("updated-radiology");
        specialtyRepository.save(specialty);

        Specialty updated = specialtyRepository.findById(1).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated-radiology");
    }
}
