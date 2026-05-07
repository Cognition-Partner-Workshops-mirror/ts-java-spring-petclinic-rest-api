package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingVet_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingVet_returnsEmpty() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findBySpecialtyId_existingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).isNotEmpty();
        assertThat(vets.stream().anyMatch(v -> v.getLastName().equals("Leary"))).isTrue();
    }

    @Test
    void findBySpecialtyId_nonExistingSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_existingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_nonExistingSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByLastName_partialMatch_returnsResults() {
        List<Vet> vets = vetRepository.searchByLastName("Cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_matchesFirstOrLastName() {
        List<Vet> vets = vetRepository.searchByName("Helen");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.searchByName("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVet_persists() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void delete_existingVet_removes() {
        long countBefore = vetRepository.count();
        vetRepository.deleteById(6);
        long countAfter = vetRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 1);
    }
}
