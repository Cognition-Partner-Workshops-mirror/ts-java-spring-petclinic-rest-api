package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
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
    void findBySpecialtyName_existingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).extracting(Vet::getLastName).contains("Leary");
    }

    @Test
    void findBySpecialtyName_nonExistingSpecialty_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCaseAndSpecialtyName_matchingBoth_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCaseAndSpecialtyName("Lear", "radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void findByLastNameContainingIgnoreCaseAndSpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCaseAndSpecialtyName("Carter", "surgery");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties_persistsCorrectly() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removesIt() {
        Vet vet = vetRepository.findById(1).orElseThrow();
        vetRepository.delete(vet);
        vetRepository.flush();
        assertThat(vetRepository.findById(1)).isEmpty();
    }
}
