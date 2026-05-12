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
 * Unit tests for VetServiceImpl.
 * Mocks repositories and mapper to isolate service business logic.
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

    private Vet jamesCarter;
    private VetResponseDto jamesCarterDto;
    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");

        jamesCarter = new Vet(1, "James", "Carter");
        jamesCarter.setSpecialties(new HashSet<>(Set.of(radiology)));

        jamesCarterDto = new VetResponseDto(1, "James", "Carter", List.of(radiologyDto));
    }

    @Test
    void getAllVets_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(jamesCarter));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesCarterDto));

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVetById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(vetMapper.toResponseDto(jamesCarter)).thenReturn(jamesCarterDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void getVetById_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void createVet_validRequest_createsSuccessfully() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(radiologyDto));
        Vet newVet = new Vet(null, "Helen", "Leary");
        Vet savedVet = new Vet(2, "Helen", "Leary");
        savedVet.setSpecialties(new HashSet<>(Set.of(radiology)));
        VetResponseDto savedDto = new VetResponseDto(2, "Helen", "Leary", List.of(radiologyDto));

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getFirstName()).isEqualTo("Helen");
    }

    @Test
    void createVet_withEmptySpecialties_createsSuccessfully() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet(null, "James", "Carter");
        Vet savedVet = new Vet(1, "James", "Carter");
        VetResponseDto savedDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_nonExistingSpecialty_throwsResourceNotFoundException() {
        SpecialtyResponseDto unknownSpecialty = new SpecialtyResponseDto(999, "unknown");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(unknownSpecialty));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void updateVet_existingId_updatesSuccessfully() {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of(radiologyDto));
        Vet updatedVet = new Vet(1, "James", "Updated");
        updatedVet.setSpecialties(new HashSet<>(Set.of(radiology)));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated", List.of(radiologyDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(updatedVet);
        when(vetMapper.toResponseDto(updatedVet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getLastName()).isEqualTo("Updated");
        verify(vetMapper).updateEntityFromDto(request, jamesCarter);
    }

    @Test
    void updateVet_nonExistingId_throwsResourceNotFoundException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_existingId_deletesAndReturnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesCarter));
        when(vetMapper.toResponseDto(jamesCarter)).thenReturn(jamesCarterDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(jamesCarter);
    }

    @Test
    void deleteVet_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.searchByLastName("Carter")).thenReturn(List.of(jamesCarter));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesCarterDto));

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialty_returnsFilteredVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(jamesCarter));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesCarterDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void createVet_withNullSpecialtyId_skipsNullIds() {
        // Specialty DTO with null ID should be skipped during resolution
        SpecialtyResponseDto nullIdSpecialty = new SpecialtyResponseDto(null, "unknown");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(nullIdSpecialty));
        Vet newVet = new Vet(null, "James", "Carter");
        Vet savedVet = new Vet(1, "James", "Carter");
        VetResponseDto savedDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }
}
