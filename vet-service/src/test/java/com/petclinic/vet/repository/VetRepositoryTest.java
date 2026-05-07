package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
    void findBySpecialtyId_existingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).isNotEmpty();
        assertThat(vets).extracting(Vet::getFirstName).contains("Helen");
    }

    @Test
    void findBySpecialtyId_nonExistingSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByNameContaining_matchingFirstName_returnsResults() {
        List<Vet> vets = vetRepository.findByNameContaining("James");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByNameContaining_matchingLastName_returnsResults() {
        List<Vet> vets = vetRepository.findByNameContaining("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContaining_caseInsensitive_returnsResults() {
        List<Vet> vets = vetRepository.findByNameContaining("james");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByNameContaining_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByNameContaining("zzz");
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
    void save_newVetWithSpecialties_persistsSuccessfully() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(specialty));

        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removesSuccessfully() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);
        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
