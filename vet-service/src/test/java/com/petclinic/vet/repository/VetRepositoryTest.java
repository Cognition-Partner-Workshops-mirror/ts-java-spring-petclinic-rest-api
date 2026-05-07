package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = specialtyRepository.findByNameIgnoreCase("radiology").orElseThrow();
        surgery = specialtyRepository.findByNameIgnoreCase("surgery").orElseThrow();
    }

    @Test
    void findAllReturnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findByIdReturnsVet() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyName() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyNames() {
        List<Vet> vets = vetRepository.findBySpecialtyNames(List.of("radiology", "surgery"));
        assertThat(vets).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void searchByNameFindsFirstName() {
        List<Vet> vets = vetRepository.searchByName("Helen");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void searchByNameFindsLastName() {
        List<Vet> vets = vetRepository.searchByName("Douglas");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Douglas");
    }

    @Test
    void saveNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void deleteVet() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        vetRepository.delete(vet);
        assertThat(vetRepository.findById(1)).isEmpty();
    }

    @Test
    void updateVet() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        vet.setFirstName("Updated");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void vetSpecialtiesAreEagerLoaded() {
        Vet helen = vetRepository.findById(2).orElseThrow();
        assertThat(helen.getSpecialties()).isNotEmpty();
    }
}
