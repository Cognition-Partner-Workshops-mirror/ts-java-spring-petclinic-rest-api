package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> result = vetRepository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
        assertThat(result.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> result = vetRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_existingSpecialty_returnsVets() {
        // Specialty 1 = radiology, assigned to vet 2 (Helen Leary) and vet 5 (Henry Stevens)
        List<Vet> result = vetRepository.findBySpecialtyId(1);
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findBySpecialtyId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName_returnsResults() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive_returnsResults() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_existingSpecialty_returnsVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive_returnsVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties_persists() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removes() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);
        Optional<Vet> result = vetRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findById_vetWithSpecialties_loadsSpecialties() {
        // Vet 3 (Linda Douglas) has surgery and dentistry
        Optional<Vet> result = vetRepository.findById(3);
        assertThat(result).isPresent();
        assertThat(result.get().getSpecialties()).hasSize(2);
    }
}
