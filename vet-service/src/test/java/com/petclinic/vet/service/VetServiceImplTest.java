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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VetServiceImpl} with mocked dependencies.
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

    private Vet jamesEntity;
    private VetResponseDto jamesResponse;
    private VetRequestDto jamesRequest;
    private Specialty radiologySpecialty;

    @BeforeEach
    void setUp() {
        radiologySpecialty = new Specialty(1, "radiology");

        jamesEntity = new Vet(1, "James", "Carter");

        jamesResponse = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        jamesRequest = new VetRequestDto("James", "Carter", List.of(1));
    }

    @Test
    void findAll_shouldReturnAllVets() {
        Vet helen = new Vet(2, "Helen", "Leary");
        VetResponseDto helenResponse = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetRepository.findAll()).thenReturn(List.of(jamesEntity, helen));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesResponse, helenResponse));

        List<VetResponseDto> result = vetService.findAll();
        assertThat(result).hasSize(2);
        verify(vetRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoVets() {
        when(vetRepository.findAll()).thenReturn(List.of());
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnVet_whenExists() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesResponse);

        VetResponseDto result = vetService.findById(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_shouldThrowNotFound_whenDoesNotExist() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_shouldSaveVetWithSpecialties() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologySpecialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(jamesEntity);
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesResponse);

        VetResponseDto result = vetService.create(jamesRequest);
        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_shouldSaveVetWithoutSpecialties() {
        VetRequestDto requestNoSpecs = new VetRequestDto("James", "Carter", null);
        when(vetRepository.save(any(Vet.class))).thenReturn(jamesEntity);
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

        VetResponseDto result = vetService.create(requestNoSpecs);
        assertThat(result.getFirstName()).isEqualTo("James");
        verify(specialtyRepository, never()).findById(any());
    }

    @Test
    void create_shouldSaveVetWithEmptySpecialtyList() {
        VetRequestDto requestEmptySpecs = new VetRequestDto("James", "Carter", Collections.emptyList());
        when(vetRepository.save(any(Vet.class))).thenReturn(jamesEntity);
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

        VetResponseDto result = vetService.create(requestEmptySpecs);
        assertThat(result).isNotNull();
        verify(specialtyRepository, never()).findById(any());
    }

    @Test
    void create_shouldThrowNotFound_whenSpecialtyDoesNotExist() {
        VetRequestDto badRequest = new VetRequestDto("James", "Carter", List.of(999));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(badRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void update_shouldModifyVetAndReassignSpecialties() {
        VetRequestDto updateRequest = new VetRequestDto("James", "Carter-Updated", List.of(1));

        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologySpecialty));
        when(vetRepository.save(jamesEntity)).thenReturn(jamesEntity);
        when(vetMapper.toResponseDto(jamesEntity)).thenReturn(jamesResponse);

        VetResponseDto result = vetService.update(1, updateRequest);
        assertThat(result).isNotNull();
        verify(vetRepository).save(jamesEntity);
    }

    @Test
    void update_shouldThrowNotFound_whenVetDoesNotExist() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, jamesRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFound_whenSpecialtyDoesNotExist() {
        VetRequestDto badRequest = new VetRequestDto("James", "Carter", List.of(999));
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(1, badRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void delete_shouldRemoveVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(jamesEntity));

        vetService.delete(1);
        verify(vetRepository).delete(jamesEntity);
    }

    @Test
    void delete_shouldThrowNotFound_whenDoesNotExist() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_shouldReturnMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart"))
            .thenReturn(List.of(jamesEntity));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesResponse));

        List<VetResponseDto> result = vetService.searchByLastName("Cart");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_shouldReturnEmptyList_whenNoMatch() {
        when(vetRepository.findByLastNameContainingIgnoreCase("xyz")).thenReturn(List.of());
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByLastName("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_shouldReturnMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology"))
            .thenReturn(List.of(jamesEntity));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(jamesResponse));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");
        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_shouldReturnEmptyList_whenNoMatch() {
        when(vetRepository.findBySpecialtyName("nonexistent")).thenReturn(List.of());
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.findBySpecialtyName("nonexistent");
        assertThat(result).isEmpty();
    }
}
