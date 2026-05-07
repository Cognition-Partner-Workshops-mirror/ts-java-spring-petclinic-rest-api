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
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("radiology");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_caseInsensitive() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
    }

    @Test
    void findByNameIgnoreCase_nonExisting_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameIn_returnsMatchingSpecialties() {
        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(result).hasSize(2);
    }

    @Test
    void findByNameIn_partialMatch_returnsSubset() {
        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("radiology", "nonexistent"));
        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameIn_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameIn(Set.of("nonexistent"));
        assertThat(result).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsSuccessfully() {
        Specialty specialty = new Specialty("ophthalmology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("ophthalmology");
    }

    @Test
    void deleteById_removesSpecialty() {
        Specialty specialty = new Specialty("toDelete");
        Specialty saved = specialtyRepository.save(specialty);
        Integer id = saved.getId();

        specialtyRepository.deleteById(id);

        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
