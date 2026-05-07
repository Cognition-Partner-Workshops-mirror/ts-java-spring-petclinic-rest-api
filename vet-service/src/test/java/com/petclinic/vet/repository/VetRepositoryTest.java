package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);

        Vet james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>(Set.of(radiology)));
        vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));
        vetRepository.save(helen);

        Vet linda = new Vet();
        linda.setFirstName("Linda");
        linda.setLastName("Douglas");
        linda.setSpecialties(new HashSet<>(Set.of(surgery)));
        vetRepository.save(linda);
    }

    @Test
    void findByLastNameContainingIgnoreCase() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyName("oncology");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyNameContaining() {
        List<Vet> result = vetRepository.findBySpecialtyNameContaining("surg");
        assertThat(result).hasSize(2);
    }

    @Test
    void findAllWithSpecialties() {
        List<Vet> result = vetRepository.findAllWithSpecialties();
        assertThat(result).hasSize(3);
        result.forEach(v -> assertThat(v.getSpecialties()).isNotEmpty());
    }

    @Test
    void save_setsAuditFields() {
        Vet v = new Vet();
        v.setFirstName("Test");
        v.setLastName("Vet");
        v.setSpecialties(new HashSet<>());
        Vet saved = vetRepository.save(v);
        vetRepository.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
