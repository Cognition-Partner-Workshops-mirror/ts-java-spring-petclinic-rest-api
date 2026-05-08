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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for VetServiceImpl business logic.
 * Covers CRUD operations, specialty assignment, and search/filtering.
 */
@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet jamesCarter;
    private VetResponseDto jamesCarterDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        jamesCarter = new Vet(1, "James", "Carter");
        radiology = new Specialty(1, "radiology");
        jamesCarterDto = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
    }

    @Test
    void getAllVets_returnsList() {
        List<Vet> vets = List.of(jamesCarter);
        List<VetResponseDto> dtos = List.of(jamesCarterDto);
        when(vetRepository.findAll()).thenReturn(vets);
        when(vetMapper.toResponseDtos(vets)).thenReturn(dtos);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVetById_found_returnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(vetMapper.toResponseDto(jamesCarter)).thenReturn(jamesCarterDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void getVetById_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void createVet_withSpecialties_savesAndReturnsDto() {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specialtyDto));
        Vet savedVet = new Vet(7, "Helen", "Leary");
        savedVet.setSpecialties(Set.of(radiology));
        VetResponseDto savedDto = new VetResponseDto(7, "Helen", "Leary", List.of(specialtyDto));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void createVet_withEmptySpecialties_savesSuccessfully() {
        VetRequestDto request = new VetRequestDto("New", "Vet", Collections.emptyList());
        Vet savedVet = new Vet(8, "New", "Vet");
        VetResponseDto savedDto = new VetResponseDto(8, "New", "Vet", Collections.emptyList());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_withNullSpecialties_savesSuccessfully() {
        VetRequestDto request = new VetRequestDto("New", "Vet", null);
        Vet savedVet = new Vet(9, "New", "Vet");
        VetResponseDto savedDto = new VetResponseDto(9, "New", "Vet", Collections.emptyList());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
    }

    @Test
    void createVet_withInvalidSpecialtyId_throwsException() {
        SpecialtyResponseDto badSpecialtyDto = new SpecialtyResponseDto(999, "nonexistent");
        VetRequestDto request = new VetRequestDto("Bad", "Vet", List.of(badSpecialtyDto));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void updateVet_found_updatesAndReturnsDto() {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(specialtyDto));
        Vet updatedVet = new Vet(1, "Updated", "Carter");
        updatedVet.setSpecialties(Set.of(radiology));
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Carter", List.of(specialtyDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(jamesCarter)).thenReturn(updatedVet);
        when(vetMapper.toResponseDto(updatedVet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_notFound_throwsException() {
        VetRequestDto request = new VetRequestDto("X", "Y", Collections.emptyList());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_found_deletesAndReturnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(vetMapper.toResponseDto(jamesCarter)).thenReturn(jamesCarterDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).delete(jamesCarter);
    }

    @Test
    void deleteVet_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsList() {
        List<Vet> vets = List.of(jamesCarter);
        List<VetResponseDto> dtos = List.of(jamesCarterDto);
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(vets);
        when(vetMapper.toResponseDtos(vets)).thenReturn(dtos);

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsList() {
        Vet helenLeary = new Vet(2, "Helen", "Leary");
        helenLeary.setSpecialties(Set.of(radiology));
        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        List<Vet> vets = List.of(helenLeary);
        List<VetResponseDto> dtos = List.of(helenDto);
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(vets);
        when(vetMapper.toResponseDtos(vets)).thenReturn(dtos);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }
}
