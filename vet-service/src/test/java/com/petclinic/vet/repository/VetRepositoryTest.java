package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Test
    void findAll_returnsSeeded() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_found() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1); // radiology
        assertThat(vets).isNotEmpty();
        assertThat(vets).extracting(Vet::getLastName)
            .contains("Leary", "Stevens");
    }

    @Test
    void findBySpecialtyId_noVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByLastName_found() {
        List<Vet> vets = vetRepository.searchByLastName("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByLastName_partialMatch() {
        List<Vet> vets = vetRepository.searchByLastName("Car");
        assertThat(vets).hasSize(1);
    }

    @Test
    void searchByLastName_caseInsensitive() {
        List<Vet> vets = vetRepository.searchByLastName("carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void searchByLastName_noMatch() {
        List<Vet> vets = vetRepository.searchByLastName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> vets = vetRepository.searchByName("James");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void searchByName_noMatch() {
        List<Vet> vets = vetRepository.searchByName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void saveAndRetrieve() {
        Vet newVet = new Vet();
        newVet.setFirstName("New");
        newVet.setLastName("Vet");
        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        Optional<Vet> retrieved = vetRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFirstName()).isEqualTo("New");
    }
}
