package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = specialtyRepository.save(createSpecialty("radiology"));
        surgery = specialtyRepository.save(createSpecialty("surgery"));

        Vet carter = new Vet();
        carter.setFirstName("James");
        carter.setLastName("Carter");
        carter.setSpecialties(Set.of(radiology));
        vetRepository.save(carter);

        Vet leary = new Vet();
        leary.setFirstName("Helen");
        leary.setLastName("Leary");
        leary.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(leary);

        Vet solo = new Vet();
        solo.setFirstName("Solo");
        solo.setLastName("Vet");
        vetRepository.save(solo);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(vets).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("LEARY");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        List<Vet> vets = vetRepository.findBySpecialtyName("surgery");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("dentistry");
        assertThat(vets).isEmpty();
    }

    @Test
    void findAll_returnsAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSize(3);
    }

    @Test
    void save_setsAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Audit");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    private Specialty createSpecialty(String name) {
        Specialty s = new Specialty();
        s.setName(name);
        return s;
    }
}
