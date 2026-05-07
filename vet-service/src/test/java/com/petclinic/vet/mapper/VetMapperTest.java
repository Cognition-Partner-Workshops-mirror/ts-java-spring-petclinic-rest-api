package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetMapperTest {

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private VetMapper vetMapper;

    @Test
    void toDto_mapsFieldsCorrectly() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(s));

        when(specialtyMapper.toDto(s)).thenReturn(new SpecialtyDto(1, "radiology"));

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
    }

    @Test
    void toDtoList_mapsAllEntities() {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(Set.of());

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(Set.of());

        List<VetDto> dtos = vetMapper.toDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void toEntity_createsVetWithSpecialties() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");

        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        Vet vet = vetMapper.toEntity(request, Set.of(s));

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void updateEntity_updatesVetFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("surgery");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "surgery")));

        vetMapper.updateEntity(request, vet, Set.of(s));

        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getSpecialties()).hasSize(1);
    }
}
