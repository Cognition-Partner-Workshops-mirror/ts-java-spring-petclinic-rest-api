package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.petclinic.vet.config.JpaAuditingConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingVet_returnsVet() {
        assertThat(vetRepository.findById(1)).isPresent();
    }

    @Test
    void findById_nonExistingVet_returnsEmpty() {
        assertThat(vetRepository.findById(999)).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> radiologyVets = vetRepository.findBySpecialtyName("radiology");
        assertThat(radiologyVets).isNotEmpty();
        radiologyVets.forEach(vet ->
            assertThat(vet.getSpecialties()).extracting(Specialty::getName).contains("radiology")
        );
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
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> vets = vetRepository.searchByName("james");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.searchByName("ZZZnonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void saveAndDelete_vet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();

        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void save_vetWithSpecialties() {
        Specialty surgery = specialtyRepository.findByNameIn(Set.of("surgery")).get(0);

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(surgery));
        Vet saved = vetRepository.save(vet);

        Vet fetched = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(fetched.getSpecialties()).hasSize(1);
        assertThat(fetched.getSpecialties().iterator().next().getName()).isEqualTo("surgery");
    }
}
