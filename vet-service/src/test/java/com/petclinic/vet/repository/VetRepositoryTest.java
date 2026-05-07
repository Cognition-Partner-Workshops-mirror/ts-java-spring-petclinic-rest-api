package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        Vet vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(List.of(radiology));
        vetRepository.save(vet1);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(List.of(surgery));
        vetRepository.save(vet2);

        List<Vet> results = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findBySpecialtyId_noMatches_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);

        assertThat(results).isEmpty();
    }

    @Test
    void searchByLastName_partialMatch_returnsResults() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.searchByLastName("Cart");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByLastName_caseInsensitive_returnsResults() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.searchByLastName("carter");

        assertThat(results).hasSize(1);
    }

    @Test
    void searchByName_matchesFirstName_returnsResults() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.searchByName("James");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_matchesLastName_returnsResults() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.searchByName("Carter");

        assertThat(results).hasSize(1);
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.searchByName("Unknown");

        assertThat(results).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Vet saved = vetRepository.save(vet);
        vetRepository.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_vetWithMultipleSpecialties_persistsRelationship() {
        Vet vet = new Vet();
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        vet.setSpecialties(List.of(radiology, surgery));

        Vet saved = vetRepository.save(vet);
        vetRepository.flush();

        Vet found = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSpecialties()).hasSize(2);
    }
}
