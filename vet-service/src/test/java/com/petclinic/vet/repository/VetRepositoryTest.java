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
@Import(JpaAuditingConfig.class)
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
    void findBySpecialtyName_existingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).allSatisfy(v ->
            assertThat(v.getSpecialties()).anyMatch(s -> s.getName().equals("radiology")));
    }

    @Test
    void findBySpecialtyName_nonExistingSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByName_matchingFirstName_returnsVets() {
        List<Vet> vets = vetRepository.searchByName("James");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_matchingLastName_returnsVets() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_caseInsensitive_returnsVets() {
        List<Vet> vets = vetRepository.searchByName("james");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.searchByName("NonExistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVet_persistsSuccessfully() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void delete_existingVet_removesIt() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        vetRepository.delete(vet);
        assertThat(vetRepository.findById(1)).isEmpty();
    }
}
