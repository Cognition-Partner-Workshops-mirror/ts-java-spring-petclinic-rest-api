package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
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
    void findAll_returnsSeedData() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        Specialty radiology = specialtyRepository.findByNameContainingIgnoreCase("radiology").getFirst();
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(result).isNotEmpty();
        result.forEach(v ->
            assertThat(v.getSpecialties()).extracting(Specialty::getName).contains("radiology")
        );
    }

    @Test
    void findBySpecialtyId_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyId(9999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties() {
        Specialty spec = specialtyRepository.findAll().getFirst();
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>(Set.of(spec)));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
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
