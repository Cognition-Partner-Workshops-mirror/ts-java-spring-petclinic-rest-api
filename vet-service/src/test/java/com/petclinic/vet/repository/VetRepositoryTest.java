package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link VetRepository} using @DataJpaTest.
 * Runs against the H2 database with Flyway migrations (including seed data).
 */
@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("findAll returns the seeded vets")
    void findAll_returnsSeedData() {
        List<Vet> all = vetRepository.findAll();
        // V2 seed data inserts 6 vets
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("findById returns a seeded vet")
    void findById_returnsSeedVet() {
        Optional<Vet> opt = vetRepository.findById(1);
        assertThat(opt).isPresent();
        assertThat(opt.get().getFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("findById returns empty for a non-existent ID")
    void findById_returnsEmpty() {
        Optional<Vet> opt = vetRepository.findById(999);
        assertThat(opt).isEmpty();
    }

    @Test
    @DisplayName("save persists a new vet with specialties and audit fields")
    void save_persistsNewVetWithSpecialties() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("Alice");
        vet.setLastName("Brown");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("findBySpecialtyId returns vets with the given specialty")
    void findBySpecialtyId_returnsMatching() {
        // Specialty 1 = radiology; Helen Leary (id 2) and Henry Stevens (id 5) have it
        List<Vet> result = vetRepository.findBySpecialtyId(1);
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("findBySpecialtyId returns empty for unused specialty")
    void findBySpecialtyId_emptyForUnused() {
        // Create a specialty no vet has
        Specialty newSpec = new Specialty();
        newSpec.setName("exotic");
        Specialty saved = specialtyRepository.save(newSpec);

        List<Vet> result = vetRepository.findBySpecialtyId(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase matches partial last names")
    void findByLastName_matchesPartial() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getLastName()).containsIgnoringCase("cart");
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase returns empty for no match")
    void findByLastName_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("searchByName matches on first name")
    void searchByName_matchesFirstName() {
        List<Vet> result = vetRepository.searchByName("James");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("searchByName matches on last name")
    void searchByName_matchesLastName() {
        List<Vet> result = vetRepository.searchByName("Carter");
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("searchByName returns empty for no match")
    void searchByName_noMatch() {
        List<Vet> result = vetRepository.searchByName("NonExistent");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("delete removes the vet")
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>());
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);
        vetRepository.flush();

        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
