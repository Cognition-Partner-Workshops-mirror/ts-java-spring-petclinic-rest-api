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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for VetServiceImpl using Mockito.
 * Covers CRUD operations, specialty assignment, and search/filter logic.
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

    private Vet sampleVet;
    private VetResponseDto sampleVetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        sampleVet = new Vet(1, "James", "Carter");
        sampleVet.setSpecialties(new HashSet<>(Set.of(radiology)));
        sampleVetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    @DisplayName("getAllVets returns all vets")
    void getAllVets_returnsAll() {
        Vet vet2 = new Vet(2, "Helen", "Leary");
        VetResponseDto vet2Dto = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.findAll()).thenReturn(List.of(sampleVet, vet2));
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(sampleVetDto);
        when(vetMapper.toResponseDto(vet2)).thenReturn(vet2Dto);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getAllVets returns empty list when no vets exist")
    void getAllVets_empty() {
        when(vetRepository.findAll()).thenReturn(List.of());
        assertThat(vetService.getAllVets()).isEmpty();
    }

    @Test
    @DisplayName("getVetById returns vet when found")
    void getVetById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(sampleVet));
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(sampleVetDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("getVetById throws ResourceNotFoundException when not found")
    void getVetById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    @DisplayName("createVet creates vet with specialties")
    void createVet_withSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(sampleVet);
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(sampleVetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    @DisplayName("createVet creates vet without specialties")
    void createVet_withoutSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet vetNoSpecialties = new Vet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpecialties);
        when(vetMapper.toResponseDto(vetNoSpecialties)).thenReturn(dto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    @DisplayName("createVet creates vet with empty specialty list")
    void createVet_emptySpecialtyList() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet vetNoSpecialties = new Vet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpecialties);
        when(vetMapper.toResponseDto(vetNoSpecialties)).thenReturn(dto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    @DisplayName("createVet throws ResourceNotFoundException for invalid specialty ID")
    void createVet_invalidSpecialtyId() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");

        verify(vetRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateVet updates vet name and specialties")
    void updateVet_success() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(sampleVet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(sampleVet)).thenReturn(sampleVet);
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("updateVet throws ResourceNotFoundException when vet not found")
    void updateVet_notFound() {
        VetRequestDto request = new VetRequestDto("Test", "Test", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteVet deletes when vet exists")
    void deleteVet_success() {
        when(vetRepository.existsById(1)).thenReturn(true);

        vetService.deleteVet(1);

        verify(vetRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteVet throws ResourceNotFoundException when not found")
    void deleteVet_notFound() {
        when(vetRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("searchByLastName returns matching vets")
    void searchByLastName_found() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(sampleVet));
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(sampleVetDto);

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    @DisplayName("searchByLastName returns empty list for no matches")
    void searchByLastName_noMatch() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Unknown"))
            .thenReturn(List.of());

        assertThat(vetService.searchByLastName("Unknown")).isEmpty();
    }

    @Test
    @DisplayName("filterBySpecialty returns matching vets")
    void filterBySpecialty_found() {
        when(vetRepository.findBySpecialtyNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(sampleVet));
        when(vetMapper.toResponseDto(sampleVet)).thenReturn(sampleVetDto);

        List<VetResponseDto> result = vetService.filterBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("filterBySpecialty returns empty list for no matches")
    void filterBySpecialty_noMatch() {
        when(vetRepository.findBySpecialtyNameContainingIgnoreCase("unknown"))
            .thenReturn(List.of());

        assertThat(vetService.filterBySpecialty("unknown")).isEmpty();
    }
}
