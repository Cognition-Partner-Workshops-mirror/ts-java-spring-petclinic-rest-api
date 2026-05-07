package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);
    }

    @Test
    void findAll_returnsAllSpecialties() {
        List<Specialty> result = specialtyRepository.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findById(radiology.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound() {
        Optional<Specialty> result = specialtyRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("dentistry");
        assertThat(result).isEmpty();
    }

    @Test
    void save_createsSpecialty() {
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        Specialty saved = specialtyRepository.save(dentistry);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("dentistry");
    }

    @Test
    void delete_removesSpecialty() {
        specialtyRepository.delete(radiology);
        assertThat(specialtyRepository.findById(radiology.getId())).isEmpty();
    }

    @Test
    void update_updatesSpecialty() {
        radiology.setName("updated-radiology");
        Specialty updated = specialtyRepository.save(radiology);
        assertThat(updated.getName()).isEqualTo("updated-radiology");
    }
}
