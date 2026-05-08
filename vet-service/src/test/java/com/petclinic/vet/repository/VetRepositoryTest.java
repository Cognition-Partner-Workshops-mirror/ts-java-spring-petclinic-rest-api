package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using an H2 in-memory database.
 * Flyway seeds the database with 6 vets and their specialty assignments.
 */
@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> all = vetRepository.findAll();
        // Seed data has 6 vets
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> result = vetRepository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
        assertThat(result.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> result = vetRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        // Specialty 1 = radiology, assigned to vet 2 (Helen Leary) and vet 5 (Henry Stevens)
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("LEARY");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Unknown");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewVetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.delete(saved);

        assertThat(vetRepository.findById(id)).isEmpty();
    }

    @Test
    void findById_vetWithSpecialties_loadedEagerly() {
        // Vet 3 (Linda Douglas) has surgery and dentistry
        Optional<Vet> result = vetRepository.findById(3);
        assertThat(result).isPresent();
        assertThat(result.get().getSpecialties()).hasSize(2);
    }
}
