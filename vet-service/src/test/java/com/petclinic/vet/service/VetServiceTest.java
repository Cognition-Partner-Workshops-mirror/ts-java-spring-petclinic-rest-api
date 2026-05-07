package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyReferenceDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetService vetService;

    private Vet james;
    private Specialty radiology;
    private VetResponseDto jamesDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);

        james = new Vet("James", "Carter");
        james.setId(1);
        james.setSpecialties(new HashSet<>());

        jamesDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        Vet helen = new Vet("Helen", "Leary");
        helen.setId(2);
        helen.setSpecialties(Set.of(radiology));
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findAllWithSpecialties()).thenReturn(List.of(james, helen));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);
        when(vetMapper.toResponseDto(helen)).thenReturn(helenDto);

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(2);
        verify(vetRepository).findAllWithSpecialties();
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findByIdWithSpecialties(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void findByLastName_matchingName_returnsVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyId_returnsVets() {
        Vet helen = new Vet("Helen", "Leary");
        helen.setId(2);
        helen.setSpecialties(Set.of(radiology));
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(helen));
        when(vetMapper.toResponseDto(helen)).thenReturn(helenDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsVets() {
        when(vetRepository.findBySpecialtyNameContainingIgnoreCase("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_validRequest_createsAndReturnsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyReferenceDto(1)));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_emptySpecialties_createsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", Collections.emptyList());

        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_nullSpecialties_createsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_nonExistingSpecialtyId_throwsNotFoundException() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyReferenceDto(999)));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");

        verify(vetRepository, never()).save(any());
    }

    @Test
    void update_existingId_updatesAndReturnsVet() {
        VetRequestDto request = new VetRequestDto("James", "Updated", Collections.emptyList());
        Vet updated = new Vet("James", "Updated");
        updated.setId(1);
        updated.setSpecialties(new HashSet<>());
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated", List.of());

        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(any(Vet.class))).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.lastName()).isEqualTo("Updated");
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", Collections.emptyList());
        when(vetRepository.findByIdWithSpecialties(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_withSpecialties_resolvesAndUpdates() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyReferenceDto(1)));

        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void delete_existingId_deletesAndReturnsVet() {
        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(vetRepository.findByIdWithSpecialties(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(vetRepository, never()).delete(any());
    }
}
