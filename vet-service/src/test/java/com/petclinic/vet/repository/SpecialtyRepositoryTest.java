package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameIgnoreCase_findsExistingSpecialty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_returnsEmptyForMissing() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameInIgnoreCase_returnsMatchingSpecialties() {
        List<Specialty> result = specialtyRepository.findByNameInIgnoreCase(List.of("Radiology", "SURGERY"));
        assertThat(result).hasSize(2);
    }

    @Test
    void findByNameContainingIgnoreCase_returnsPartialMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("ry");
        assertThat(result).extracting(Specialty::getName)
            .contains("surgery", "dentistry");
    }

    @Test
    void save_persistsNewSpecialtyWithAuditFields() {
        Specialty specialty = new Specialty();
        specialty.setName("orthopedics");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }
}
