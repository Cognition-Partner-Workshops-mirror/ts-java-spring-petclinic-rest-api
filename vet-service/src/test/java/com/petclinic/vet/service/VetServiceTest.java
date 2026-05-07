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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private VetServiceImpl vetService;

    private Vet vet;
    private Specialty specialty;
    private VetResponseDto vetResponseDto;
    private VetRequestDto vetRequestDto;
    private SpecialtyResponseDto specialtyResponseDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty("radiology");
        specialty.setId(1);
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());

        vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        vet.setSpecialties(Set.of(specialty));

        specialtyResponseDto = new SpecialtyResponseDto(1, "radiology");
        vetResponseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyResponseDto));
        vetRequestDto = new VetRequestDto("James", "Carter", List.of(specialtyResponseDto));
    }

    @Test
    void findAll_shouldReturnAllVets() {
        when(vetRepository.findAllWithSpecialties()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_shouldReturnVet() {
        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(vetRepository.findByIdWithSpecialties(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_shouldSaveAndReturnVet() {
        when(specialtyRepository.findByIdIn(List.of(1))).thenReturn(List.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.create(vetRequestDto);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.lastName()).isEqualTo("Carter");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties_shouldSaveVet() {
        VetRequestDto dtoNoSpecialties = new VetRequestDto("James", "Carter", List.of());
        Vet vetNoSpecialties = new Vet("James", "Carter");
        vetNoSpecialties.setId(1);
        vetNoSpecialties.setCreatedAt(LocalDateTime.now());
        vetNoSpecialties.setUpdatedAt(LocalDateTime.now());
        VetResponseDto responseNoSpecialties = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpecialties);
        when(vetMapper.toResponseDto(vetNoSpecialties)).thenReturn(responseNoSpecialties);

        VetResponseDto result = vetService.create(dtoNoSpecialties);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_shouldThrowWhenSpecialtyNotFound() {
        SpecialtyResponseDto unknownSpecialty = new SpecialtyResponseDto(999, "unknown");
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(unknownSpecialty));

        when(specialtyRepository.findByIdIn(List.of(999))).thenReturn(List.of());

        assertThatThrownBy(() -> vetService.create(dto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateAndReturnVet() {
        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByIdIn(List.of(1))).thenReturn(List.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.update(1, vetRequestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vet);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(vetRepository.findByIdWithSpecialties(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, vetRequestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteAndReturnVet() {
        when(vetRepository.findByIdWithSpecialties(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(vetRepository.findByIdWithSpecialties(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_shouldReturnVetsWithSpecialty() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_shouldReturnMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }
}
