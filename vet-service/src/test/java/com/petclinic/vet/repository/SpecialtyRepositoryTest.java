package com.petclinic.vet.repository;

import com.petclinic.vet.entity.SpecialtyEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findByNameIgnoreCase_findsExact() {
        Optional<SpecialtyEntity> result = repository.findByNameIgnoreCase("radiology");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_isCaseInsensitive() {
        Optional<SpecialtyEntity> result = repository.findByNameIgnoreCase("RADIOLOGY");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_returnsEmptyForUnknown() {
        Optional<SpecialtyEntity> result = repository.findByNameIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_returnsTrueForExisting() {
        assertThat(repository.existsByNameIgnoreCase("surgery")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_isCaseInsensitive() {
        assertThat(repository.existsByNameIgnoreCase("SURGERY")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_returnsFalseForUnknown() {
        assertThat(repository.existsByNameIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    void save_setsAuditFields() {
        SpecialtyEntity entity = new SpecialtyEntity();
        entity.setName("oncology");
        SpecialtyEntity saved = repository.saveAndFlush(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
