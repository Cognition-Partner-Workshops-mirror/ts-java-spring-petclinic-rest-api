package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        // specialty 1 = radiology, assigned to vets 2 and 5
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartial() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContainingIgnoreCase_matchesFirstName() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("James");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_matchesLastName() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("Douglas");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("zzz");
        assertThat(results).isEmpty();
    }

    @Test
    void findById_vetWithSpecialties_eagarlyLoadsSpecialties() {
        // Vet 3 (Linda Douglas) has surgery and dentistry
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(2);
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);

        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
