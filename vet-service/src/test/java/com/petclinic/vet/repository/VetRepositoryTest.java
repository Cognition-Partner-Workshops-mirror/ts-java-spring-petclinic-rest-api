package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Set;
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

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_returnsVet() {
        assertThat(vetRepository.findById(1)).isPresent();
        assertThat(vetRepository.findById(1).get().getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_notFound() {
        assertThat(vetRepository.findById(999)).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).allSatisfy(v ->
            assertThat(v.getSpecialties()).extracting(Specialty::getName).contains("radiology")
        );
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_matchesFirstName() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("james");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByNameContainingIgnoreCase_matchesLastName() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("carter");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("zzzzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void saveVet_persistsNewVet() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(radiology));

        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void deleteVet_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
