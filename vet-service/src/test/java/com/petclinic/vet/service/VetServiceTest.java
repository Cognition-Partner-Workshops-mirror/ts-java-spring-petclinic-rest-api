package com.petclinic.vet.service;

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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for VetServiceImpl with mocked repository and mapper.
 */
@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private VetResponseDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        james = new Vet(1, "James", "Carter");
        james.setSpecialties(Set.of(radiology));
        jamesDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() {
        Vet helen = new Vet(2, "Helen", "Leary");
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary", List.of());
        when(vetRepository.findAll()).thenReturn(List.of(james, helen));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);
        when(vetMapper.toResponseDto(helen)).thenReturn(helenDto);

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(2);
    }

    @Test
    void getVet_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void getVet_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void addVet_validRequest_createsAndReturns() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(1));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(10);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(
            new VetResponseDto(10, "New", "Vet",
                List.of(new SpecialtyResponseDto(1, "radiology"))));

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getFirstName()).isEqualTo("New");
    }

    @Test
    void addVet_emptySpecialtyList_createsVetWithNoSpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", List.of());
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(11);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(
            new VetResponseDto(11, "Solo", "Vet", List.of()));

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void addVet_nullSpecialtyList_createsVetWithNoSpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", null);
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet v = invocation.getArgument(0);
            v.setId(12);
            return v;
        });
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(
            new VetResponseDto(12, "Solo", "Vet", List.of()));

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void addVet_invalidSpecialtyId_throwsResourceNotFoundException() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(999));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.addVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void updateVet_existingId_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(
            new VetResponseDto(1, "Updated", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology"))));

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_nonExistingId_throwsResourceNotFoundException() {
        VetRequestDto request = new VetRequestDto("X", "Y", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_existingId_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart"))
            .thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void filterBySpecialtyId_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.filterBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyNameContainingIgnoreCase("radio"))
            .thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.filterBySpecialtyName("radio");

        assertThat(result).hasSize(1);
    }
}
