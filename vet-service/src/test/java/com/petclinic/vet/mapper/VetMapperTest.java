package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = new VetMapperImpl();

    @Test
    void specialtiesToDtoList_withSpecialties_returnsDtoList() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "radiology"));
        specialties.add(new Specialty(2, "surgery"));

        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(specialties);

        assertThat(result).hasSize(2);
    }

    @Test
    void specialtiesToDtoList_withNull_returnsEmptyList() {
        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(null);
        assertThat(result).isEmpty();
    }
}
