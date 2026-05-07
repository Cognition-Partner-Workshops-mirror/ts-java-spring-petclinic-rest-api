package com.petclinic.vet.repository;

import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.entity.VetEntity;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
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

    private SpecialtyEntity radiology;
    private SpecialtyEntity surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = specialtyRepository.findByNameIgnoreCase("radiology")
            .orElseGet(() -> {
                SpecialtyEntity s = new SpecialtyEntity();
                s.setName("radiology");
                return specialtyRepository.save(s);
            });

        surgery = specialtyRepository.findByNameIgnoreCase("surgery")
            .orElseGet(() -> {
                SpecialtyEntity s = new SpecialtyEntity();
                s.setName("surgery");
                return specialtyRepository.save(s);
            });

        VetEntity vet1 = new VetEntity();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(Set.of(radiology));
        vetRepository.save(vet1);

        VetEntity vet2 = new VetEntity();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(vet2);

        VetEntity vet3 = new VetEntity();
        vet3.setFirstName("Linda");
        vet3.setLastName("Douglas");
        vetRepository.save(vet3);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<VetEntity> result = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(VetEntity::getLastName)
            .containsExactlyInAnyOrder("Carter", "Leary");
    }

    @Test
    void findBySpecialtyId_returnsEmptyForUnknown() {
        List<VetEntity> result = vetRepository.findBySpecialtyId(9999);

        assertThat(result).isEmpty();
    }

    @Test
    void searchByLastName_returnsMatching() {
        List<VetEntity> result = vetRepository.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByLastName_isCaseInsensitive() {
        List<VetEntity> result = vetRepository.searchByLastName("cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByLastName_returnsEmptyForNoMatch() {
        List<VetEntity> result = vetRepository.searchByLastName("ZZZ");

        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatching() {
        List<VetEntity> result = vetRepository.findBySpecialtyName("surgery");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void findBySpecialtyName_isCaseInsensitive() {
        List<VetEntity> result = vetRepository.findBySpecialtyName("RADIOLOGY");

        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_returnsEmptyForUnknown() {
        List<VetEntity> result = vetRepository.findBySpecialtyName("nonexistent");

        assertThat(result).isEmpty();
    }
}
