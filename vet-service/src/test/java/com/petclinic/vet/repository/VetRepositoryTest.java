package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatchingVets() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsEmptyForNoMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("zzzzz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(vet ->
            assertThat(vet.getSpecialties()).extracting(Specialty::getName).contains("radiology"));
    }

    @Test
    void findBySpecialtyName_returnsEmptyForNonExistingSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> result = vetRepository.searchByName("Jam");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> result = vetRepository.searchByName("Lear");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void findBySpecialtyNameIn_returnsVetsWithAnyMatchingSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyNameIn(List.of("radiology", "surgery"));
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void save_persistsNewVetWithAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty radiology = specialtyRepository.findByNameIgnoreCase("radiology").orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getSpecialties()).hasSize(1);
        assertThat(saved.getSpecialties()).extracting(Specialty::getName).contains("radiology");
    }

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }
}
