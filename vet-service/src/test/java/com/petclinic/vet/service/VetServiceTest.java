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
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private Vet vet;
    private Specialty specialty;
    private VetResponseDto responseDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(Instant.now());
        specialty.setUpdatedAt(Instant.now());

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        responseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void createVet_validRequest_returnsCreated() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void createVet_withNonExistingSpecialty_throwsNotFound() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(999, "nonexistent")));

        Vet newVet = new Vet();
        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void createVet_withEmptySpecialties_succeeds() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of());

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(requestDto);

        assertThat(result).isNotNull();
    }

    @Test
    void updateVet_existingId_returnsUpdated() {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(
            new VetResponseDto(1, "Updated", "Vet", List.of(new SpecialtyResponseDto(1, "radiology"))));

        VetResponseDto result = vetService.updateVet(1, requestDto);

        assertThat(result.firstName()).isEqualTo("Updated");
        verify(vetMapper).updateEntity(eq(requestDto), eq(vet));
    }

    @Test
    void updateVet_nonExistingId_throwsNotFound() {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Vet", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_existingId_returnsDeleted() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsFilteredVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.findByNameContaining("James")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
