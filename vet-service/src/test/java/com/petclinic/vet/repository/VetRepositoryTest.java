package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

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
    private Vet james;
    private Vet helen;

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

        james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));
        james = vetRepository.save(james);

        helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(Set.of(radiology, surgery));
        helen = vetRepository.save(helen);
    }

    @Test
    void findByLastNameContainingIgnoreCase_existingName_returnsVets() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialName_returnsVets() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("ar");

        assertThat(results).hasSize(2);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_existingId_returnsVets() {
        List<Vet> results = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyId_surgeryOnly_returnsHelen() {
        List<Vet> results = vetRepository.findBySpecialtyId(surgery.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_existingName_returnsVets() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");

        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findBySpecialtyName("dentistry");

        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameAndSpecialtyName_matchesBoth_returnsVets() {
        List<Vet> results = vetRepository.findByLastNameAndSpecialtyName("Carter", "radiology");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameAndSpecialtyName_noMatch_returnsEmpty() {
        List<Vet> results = vetRepository.findByLastNameAndSpecialtyName("Carter", "surgery");

        assertThat(results).isEmpty();
    }

    @Test
    void save_newVet_setsAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Vet vet = new Vet();
        vet.setFirstName("Rafael");
        vet.setLastName("Ortega");
        vet.setSpecialties(Set.of(surgery));
        Vet saved = vetRepository.save(vet);

        Vet found = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSpecialties()).hasSize(1);
    }
}
