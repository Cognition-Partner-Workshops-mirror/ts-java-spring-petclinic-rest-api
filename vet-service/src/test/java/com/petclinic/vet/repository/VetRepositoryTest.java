package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.petclinic.vet.config.JpaAuditingConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using @DataJpaTest.
 * Uses H2 in-memory database with Flyway seed data.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        // V2 seed data includes 6 vets
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
    void findByLastNameContainingIgnoreCase_matchingName_returnsResults() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findByFirstNameContainingIgnoreCase_returnsResults() {
        List<Vet> results = vetRepository.findByFirstNameContainingIgnoreCase("hel");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyId_existingSpecialty_returnsVets() {
        // Specialty 1 (radiology) is assigned to vets 2 (Helen Leary) and 5 (Henry Stevens)
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyId_nonExistingSpecialty_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_matchingName_returnsVets() {
        // "surgery" is assigned to vets 3 (Linda Douglas) and 4 (Rafael Ortega)
        List<Vet> results = vetRepository.findBySpecialtyNameContainingIgnoreCase("surgery");
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties_persistsRelationship() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        // Verify the many-to-many relationship was persisted
        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removesFromDatabase() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);

        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
