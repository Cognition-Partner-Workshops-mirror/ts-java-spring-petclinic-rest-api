package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> found = vetRepository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("James");
        assertThat(found.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> found = vetRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void save_newVet_persistsSuccessfully() {
        Vet v = new Vet();
        v.setFirstName("Test");
        v.setLastName("Vet");
        Vet saved = vetRepository.save(v);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet v = new Vet();
        v.setFirstName("New");
        v.setLastName("Doctor");
        v.setSpecialties(new HashSet<>(Set.of(specialty)));
        Vet saved = vetRepository.save(v);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatching() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("SURGERY");
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findByLastNameAndSpecialtyName_returnsMatching() {
        List<Vet> results = vetRepository.findByLastNameAndSpecialtyName("Leary", "radiology");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByLastNameAndSpecialtyName_noMatch() {
        List<Vet> results = vetRepository.findByLastNameAndSpecialtyName("Carter", "surgery");
        assertThat(results).isEmpty();
    }

    @Test
    void delete_removesVet() {
        Vet v = new Vet();
        v.setFirstName("Del");
        v.setLastName("Me");
        Vet saved = vetRepository.save(v);
        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void vetSpecialties_eagerLoaded() {
        Vet vet = vetRepository.findById(3).orElseThrow();
        assertThat(vet.getSpecialties()).hasSize(2);
    }
}
