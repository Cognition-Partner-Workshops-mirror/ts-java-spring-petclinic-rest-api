package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using @DataJpaTest.
 * Uses H2 in-memory database with Flyway migrations and seed data.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedVets() {
        // Seed data from V4 migration includes 6 vets
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
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
    void searchByLastName_partialMatch_returnsMatching() {
        List<Vet> result = vetRepository.searchByLastName("Cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByLastName_caseInsensitive_returnsMatching() {
        List<Vet> result = vetRepository.searchByLastName("carter");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.searchByLastName("Nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_existingSpecialty_returnsVetsWithSpecialty() {
        // Helen Leary (id=2) and Henry Stevens (id=5) have radiology specialty
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_nonExistingSpecialty_returnsEmpty() {
        List<Vet> result = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameAndSpecialtyName_matchingBoth_returnsFiltered() {
        // Helen Leary has radiology
        List<Vet> result = vetRepository.findByLastNameAndSpecialtyName("Leary", "radiology");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void findByLastNameAndSpecialtyName_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findByLastNameAndSpecialtyName("Carter", "radiology");
        // James Carter has no specialties in seed data
        assertThat(result).isEmpty();
    }

    @Test
    void save_newVet_assignsId() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));
        Vet saved = vetRepository.save(vet);

        // Re-fetch to verify the many-to-many relationship persisted
        Vet fetched = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(fetched.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removesFromDatabase() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);

        Optional<Vet> result = vetRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_caseInsensitive_returnsMatching() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }
}
