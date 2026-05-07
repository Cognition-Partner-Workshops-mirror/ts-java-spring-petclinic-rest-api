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
    private Vet vet1;
    private Vet vet2;

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

        vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(new HashSet<>(Set.of(radiology)));
        vet1 = vetRepository.save(vet1);

        vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));
        vet2 = vetRepository.save(vet2);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_found() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("LEAR");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_found() {
        List<Vet> result = vetRepository.findBySpecialtyName("surgery");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyName("dentistry");
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllVets() {
        List<Vet> result = vetRepository.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void save_persistsVet() {
        Vet newVet = new Vet();
        newVet.setFirstName("Linda");
        newVet.setLastName("Douglas");
        newVet.setSpecialties(new HashSet<>());
        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void delete_removesVet() {
        vetRepository.delete(vet1);
        List<Vet> result = vetRepository.findAll();
        assertThat(result).hasSize(1);
    }
}
