package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    void findById_vetWithSpecialties_returnsSpecialties() {
        Optional<Vet> vet = vetRepository.findById(2);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(1);
        assertThat(vet.get().getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_vetWithMultipleSpecialties_returnsAll() {
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(2);
    }

    @Test
    void save_newVet_persists() {
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("New");
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Doctor");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));
        Vet saved = vetRepository.save(vet);

        Vet fetched = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(fetched.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removes() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.delete(saved);

        assertThat(vetRepository.findById(id)).isEmpty();
    }

    @Test
    void findBySpecialtyId_matchingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialMatch_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("CARTER");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_matchingName_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> vets = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }
}
