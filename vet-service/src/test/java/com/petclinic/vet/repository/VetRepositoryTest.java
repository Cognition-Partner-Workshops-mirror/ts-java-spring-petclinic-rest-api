package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository repository;

    @Test
    void findAll_returnsSeededData() {
        List<Vet> vets = repository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> vet = repository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> vet = repository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        List<Vet> vets = repository.findBySpecialtyId(1); // radiology
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> vets = repository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> vets = repository.findBySpecialtyName("surgery");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> vets = repository.findBySpecialtyName("SURGERY");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> vets = repository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContainingIgnoreCase_matchesFirstName() {
        List<Vet> vets = repository.findByNameContainingIgnoreCase("James");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findByNameContainingIgnoreCase_matchesLastName() {
        List<Vet> vets = repository.findByNameContainingIgnoreCase("Carter");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = repository.findByNameContainingIgnoreCase("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = repository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void findById_returnsVetWithSpecialties() {
        Optional<Vet> vet = repository.findById(3); // Linda Douglas has surgery + dentistry
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(2);
    }
}
