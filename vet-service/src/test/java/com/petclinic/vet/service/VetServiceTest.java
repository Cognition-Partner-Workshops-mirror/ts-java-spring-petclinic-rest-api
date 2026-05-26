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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VetServiceImpl}.
 * Uses Mockito to isolate the service layer from repositories and mapper.
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

    private Vet jamesEntity;
    private VetResponseDto jamesDto;
    private Specialty radiologySpecialty;

    @BeforeEach
    void setUp() {
        // Prepare shared test fixtures
        radiologySpecialty = new Specialty();
        radiologySpecialty.setId(1);
        radiologySpecialty.setName("radiology");

        jamesEntity = new Vet();
        jamesEntity.setId(1);
        jamesEntity.setFirstName("James");
        jamesEntity.setLastName("Carter");
        jamesEntity.setSpecialties(new HashSet<>(Set.of(radiologySpecialty)));

        jamesDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    @DisplayName("getAllVets returns all vets from repository")
    void getAllVets_returnsList() {
        when(vetRepository.findAll()).thenReturn(List.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("getAllVets returns empty list when no vets exist")
    void getAllVets_returnsEmptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getVetById returns the vet when found")
    void getVetById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("getVetById throws ResourceNotFoundException when not found")
    void getVetById_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    @DisplayName("createVet persists and returns the new vet with specialties")
    void createVet_savesAndReturns() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));

        Vet savedEntity = new Vet();
        savedEntity.setId(2);
        savedEntity.setFirstName("Helen");
        savedEntity.setLastName("Leary");
        savedEntity.setSpecialties(new HashSet<>(Set.of(radiologySpecialty)));

        VetResponseDto expectedDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologySpecialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toResponseDto(savedEntity)).thenReturn(expectedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getFirstName()).isEqualTo("Helen");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("createVet with empty specialty list succeeds")
    void createVet_emptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        Vet savedEntity = new Vet();
        savedEntity.setId(3);
        savedEntity.setFirstName("James");
        savedEntity.setLastName("Carter");
        savedEntity.setSpecialties(new HashSet<>());

        VetResponseDto expectedDto = new VetResponseDto(3, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedEntity);
        when(vetMapper.toResponseDto(savedEntity)).thenReturn(expectedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    @DisplayName("createVet throws when a referenced specialty does not exist")
    void createVet_specialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(999));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    @DisplayName("updateVet updates the vet's fields and specialties")
    void updateVet_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Jim", "Carter", List.of(1));
        VetResponseDto updatedDto = new VetResponseDto(1, "Jim", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologySpecialty));
        when(vetRepository.save(jamesEntity)).thenReturn(jamesEntity);
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Jim");
    }

    @Test
    @DisplayName("updateVet throws ResourceNotFoundException when vet ID not found")
    void updateVet_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            vetService.updateVet(999, new VetRequestDto("X", "Y", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateVet throws when a referenced specialty does not exist")
    void updateVet_specialtyNotFound() {
        VetRequestDto request = new VetRequestDto("Jim", "Carter", List.of(999));
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(1, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    @DisplayName("deleteVet removes the entity and returns it")
    void deleteVet_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(jamesEntity);
    }

    @Test
    @DisplayName("deleteVet throws ResourceNotFoundException when not found")
    void deleteVet_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findBySpecialtyId returns vets with the given specialty")
    void findBySpecialtyId_returnsList() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("searchByName returns matching vets")
    void searchByName_returnsMatches() {
        when(vetRepository.searchByName("Carter")).thenReturn(List.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.searchByName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    @DisplayName("searchByName returns empty list when no match")
    void searchByName_noMatch() {
        when(vetRepository.searchByName("NonExistent")).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByName("NonExistent");

        assertThat(result).isEmpty();
    }
}
