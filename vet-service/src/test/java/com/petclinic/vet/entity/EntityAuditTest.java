package com.petclinic.vet.entity;

import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EntityAuditTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private VetRepository vetRepository;

    @Test
    void specialty_prePersist_setsTimestamps() {
        Specialty s = new Specialty();
        s.setName("dentistry");
        Specialty saved = specialtyRepository.save(s);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void specialty_preUpdate_updatesTimestamp() {
        Specialty s = new Specialty();
        s.setName("dentistry");
        Specialty saved = specialtyRepository.saveAndFlush(s);

        var originalUpdatedAt = saved.getUpdatedAt();
        saved.setName("updated-dentistry");
        Specialty updated = specialtyRepository.saveAndFlush(saved);

        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void vet_prePersist_setsTimestamps() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of());
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void vet_preUpdate_updatesTimestamp() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of());
        Vet saved = vetRepository.saveAndFlush(vet);

        saved.setFirstName("Updated");
        Vet updated = vetRepository.saveAndFlush(saved);

        assertThat(updated.getUpdatedAt()).isNotNull();
    }
}
