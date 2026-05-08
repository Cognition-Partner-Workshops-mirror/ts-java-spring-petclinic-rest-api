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
 * Repository-layer tests for VetRepository using @DataJpaTest.
 * Validates custom query methods for specialty and name search.
 */
@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        // V2 migration seeds 6 vets
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSize(6);
    }

    @Test
    void findById_returnsVetWithSpecialties() {
        // Vet 2 (Helen Leary) has radiology specialty
        Optional<Vet> vet = vetRepository.findById(2);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("Helen");
        assertThat(vet.get().getSpecialties()).hasSize(1);
    }

    @Test
    void findById_vetWithNoSpecialties() {
        // Vet 1 (James Carter) has no specialties
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getSpecialties()).isEmpty();
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByFirstNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> results = vetRepository.findByFirstNameContainingIgnoreCase("hel");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        // Specialty 1 (radiology) is assigned to vets 2 and 5
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_returnsVetsWithMatchingSpecialty() {
        // "surgery" is assigned to vets 3 and 4
        List<Vet> results = vetRepository.findBySpecialtyName("surgery");
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("SURGERY");
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewVetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
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

        vetRepository.delete(saved);

        Optional<Vet> deleted = vetRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
