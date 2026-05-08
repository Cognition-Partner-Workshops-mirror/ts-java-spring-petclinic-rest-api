package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for VetMapper using the real MapStruct-generated implementation.
 * Verifies entity-to-DTO conversion and alphabetical sorting of specialties.
 */
@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    /**
     * Helper to create a Specialty entity with the given id and name.
     */
    private Specialty createSpecialty(int id, String name) {
        Specialty s = new Specialty();
        s.setId(id);
        s.setName(name);
        s.setCreatedAt(Instant.now());
        s.setUpdatedAt(Instant.now());
        return s;
    }

    /**
     * Helper to create a Vet entity with the given properties and specialties.
     */
    private Vet createVet(int id, String first, String last, Set<Specialty> specialties) {
        Vet v = new Vet();
        v.setId(id);
        v.setFirstName(first);
        v.setLastName(last);
        v.setSpecialties(specialties);
        v.setCreatedAt(Instant.now());
        v.setUpdatedAt(Instant.now());
        return v;
    }

    @Test
    void toDto_mapsAllFields() {
        Specialty radiology = createSpecialty(1, "radiology");
        Vet vet = createVet(1, "James", "Carter", Set.of(radiology));

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toDto_sortsSpecialtiesAlphabetically() {
        // Specialties in non-alphabetical order
        Specialty surgery = createSpecialty(2, "surgery");
        Specialty dentistry = createSpecialty(3, "dentistry");
        Specialty radiology = createSpecialty(1, "radiology");
        Vet vet = createVet(1, "Linda", "Douglas", Set.of(surgery, dentistry, radiology));

        VetDto dto = vetMapper.toDto(vet);

        // Verify sorted alphabetically by name
        List<String> names = dto.getSpecialties().stream()
            .map(SpecialtyDto::getName)
            .toList();
        assertThat(names).containsExactly("dentistry", "radiology", "surgery");
    }

    @Test
    void toDto_withNoSpecialties_returnsEmptyList() {
        Vet vet = createVet(1, "James", "Carter", new HashSet<>());

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toDto_withNullSpecialties_returnsEmptyList() {
        Vet vet = createVet(1, "James", "Carter", null);
        vet.setSpecialties(null);

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toDtoList_convertsMultipleVets() {
        Vet vet1 = createVet(1, "James", "Carter", new HashSet<>());
        Vet vet2 = createVet(2, "Helen", "Leary", new HashSet<>());

        List<VetDto> dtos = vetMapper.toDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toDtoList_emptyCollection_returnsEmptyList() {
        List<VetDto> dtos = vetMapper.toDtoList(List.of());

        assertThat(dtos).isEmpty();
    }

    @Test
    void mapSortedSpecialties_withNullSet_returnsEmptyList() {
        List<SpecialtyDto> result = vetMapper.mapSortedSpecialties(null);

        assertThat(result).isEmpty();
    }
}
