package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void searchByName_partialMatch_returnsResults() {
        List<Specialty> results = specialtyRepository.searchByName("surg");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Specialty> results = specialtyRepository.searchByName("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_existingName_returnsTrue() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("Surgery")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_nonExistingName_returnsFalse() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("cardiology")).isFalse();
    }

    @Test
    void save_newSpecialty_persists() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void findAll_returnsSeededData() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void deleteById_removesSpecialty() {
        long countBefore = specialtyRepository.count();
        specialtyRepository.deleteById(1);
        long countAfter = specialtyRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 1);
    }
}
