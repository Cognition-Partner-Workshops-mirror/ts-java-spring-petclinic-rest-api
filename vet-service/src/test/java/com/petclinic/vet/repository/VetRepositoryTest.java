package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.config.JpaAuditingConfig;
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
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).isNotEmpty();
        assertThat(results).allSatisfy(vet ->
            assertThat(vet.getSpecialties()).anyMatch(s -> s.getId().equals(1))
        );
    }

    @Test
    void findBySpecialtyId_nonExistingSpecialty_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_partial() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_existingSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> results = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties() {
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
    void delete_existingVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);

        Optional<Vet> result = vetRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
