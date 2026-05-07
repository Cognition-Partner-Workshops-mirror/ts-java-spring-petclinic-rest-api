package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_returnsSeedVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsVet() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("CART");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_findsByFirstName() {
        List<Vet> vets = vetRepository.searchByName("James");
        assertThat(vets).hasSize(1);
    }

    @Test
    void searchByName_findsByLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void searchByName_noMatch() {
        List<Vet> vets = vetRepository.searchByName("Nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_createsNewVet() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void save_createsVetWithoutSpecialties() {
        Vet vet = new Vet();
        vet.setFirstName("Solo");
        vet.setLastName("Vet");

        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).isEmpty();
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);
        vetRepository.flush();

        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
