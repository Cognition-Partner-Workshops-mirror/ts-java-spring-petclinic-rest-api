package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
        radiology = specialtyRepository.findAll().stream()
            .filter(s -> "radiology".equals(s.getName()))
            .findFirst().orElseThrow();
        surgery = specialtyRepository.findAll().stream()
            .filter(s -> "surgery".equals(s.getName()))
            .findFirst().orElseThrow();
    }

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).allMatch(v -> v.getSpecialties().stream()
            .anyMatch(s -> "radiology".equals(s.getName())));
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> vets = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> vets = vetRepository.searchByName("James");
        assertThat(vets).isNotEmpty();
        assertThat(vets).anyMatch(v -> "James".equals(v.getFirstName()));
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).isNotEmpty();
        assertThat(vets).anyMatch(v -> "Carter".equals(v.getLastName()));
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> vets = vetRepository.searchByName("james");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_partialMatch() {
        List<Vet> vets = vetRepository.searchByName("am");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.searchByName("zzzzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVet_persistsSuccessfully() {
        Vet newVet = new Vet("Test", "Vet");
        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Vet newVet = new Vet("Test", "Specialist");
        newVet.addSpecialty(radiology);
        Vet saved = vetRepository.save(newVet);

        Vet retrieved = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(retrieved.getSpecialties()).hasSize(1);
        assertThat(retrieved.getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }

    @Test
    void deleteById_removesVet() {
        Vet newVet = new Vet("ToDelete", "Vet");
        Vet saved = vetRepository.save(newVet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);

        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
