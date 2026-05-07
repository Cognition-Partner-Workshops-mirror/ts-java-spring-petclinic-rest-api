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
    void findByNameContainingIgnoreCase_matchesPartialName() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(specialties).hasSize(1);
        assertThat(specialties.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(specialties).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("oncology");
        assertThat(specialties).isEmpty();
    }

    @Test
    void findByNameIn_returnsMatchingSpecialties() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(specialties).hasSize(2);
    }

    @Test
    void findByNameIn_noMatch() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("oncology"));
        assertThat(specialties).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsSuccessfully() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_existingSpecialty_removesFromDb() {
        Specialty specialty = new Specialty();
        specialty.setName("temporary");
        specialty = specialtyRepository.save(specialty);
        Integer id = specialty.getId();

        specialtyRepository.delete(specialty);
        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
