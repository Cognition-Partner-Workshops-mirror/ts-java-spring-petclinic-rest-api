package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty s = new Specialty("radiology");
        s.setId(1);
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s);

        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(specialties);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_withEmptySpecialties() {
        Vet vet = new Vet("Helen", "Leary");
        vet.setId(2);
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_withNullSpecialties() {
        Vet vet = new Vet("Helen", "Leary");
        vet.setId(2);
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void mapSpecialties_withNull_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(null);
        assertThat(result).isEmpty();
    }

    @Test
    void mapSpecialties_withMultipleSpecialties() {
        Specialty s1 = new Specialty("radiology");
        s1.setId(1);
        Specialty s2 = new Specialty("surgery");
        s2.setId(2);

        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s1);
        specialties.add(s2);

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(specialties);

        assertThat(result).hasSize(2);
    }
}
