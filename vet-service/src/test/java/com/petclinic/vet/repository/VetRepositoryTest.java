package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.config.JpaAuditConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using @DataJpaTest.
 * Tests custom query methods for filtering and searching.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        // Create specialties
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);

        // Create vets with specialties
        Vet james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>());
        vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(Set.of(radiology));
        vetRepository.save(helen);

        Vet linda = new Vet();
        linda.setFirstName("Linda");
        linda.setLastName("Douglas");
        linda.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(linda);
    }

    @Test
    void findAll_shouldReturnAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSize(3);
    }

    @Test
    void findBySpecialtyName_shouldFilterBySpecialty() {
        List<Vet> radiologists = vetRepository.findBySpecialtyName("radiology");
        assertThat(radiologists).hasSize(2);
    }

    @Test
    void findBySpecialtyName_shouldBeCaseInsensitive() {
        List<Vet> radiologists = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(radiologists).hasSize(2);
    }

    @Test
    void findBySpecialtyName_shouldReturnEmptyForUnknownSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_shouldFindByLastName() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContaining_shouldSearchByFirstOrLastName() {
        List<Vet> results = vetRepository.findByNameContaining("lin");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Linda");
    }

    @Test
    void findByNameContaining_shouldReturnEmptyForNoMatch() {
        List<Vet> results = vetRepository.findByNameContaining("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void save_shouldPersistNewVet() {
        Vet newVet = new Vet();
        newVet.setFirstName("Rafael");
        newVet.setLastName("Ortega");
        newVet.setSpecialties(Set.of(surgery));
        Vet saved = vetRepository.save(newVet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void deleteById_shouldRemoveVet() {
        Vet vet = vetRepository.findAll().get(0);
        vetRepository.deleteById(vet.getId());
        assertThat(vetRepository.findById(vet.getId())).isEmpty();
    }
}
