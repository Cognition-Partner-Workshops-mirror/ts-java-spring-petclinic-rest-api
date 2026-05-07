package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Vet james;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);

        james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>(Set.of(radiology)));
        james = vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));
        vetRepository.save(helen);
    }

    @Test
    void findAll_returnsAllVets() {
        List<Vet> result = vetRepository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsVet() {
        Optional<Vet> result = vetRepository.findById(james.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> result = vetRepository.searchByName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> result = vetRepository.searchByName("Helen");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> result = vetRepository.searchByName("carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsEmptyForNoMatch() {
        List<Vet> result = vetRepository.searchByName("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        assertThat(james.getCreatedAt()).isNotNull();
        assertThat(james.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_specialtiesRelationship() {
        Optional<Vet> result = vetRepository.findById(james.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getSpecialties()).hasSize(1);
        assertThat(result.get().getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }
}
