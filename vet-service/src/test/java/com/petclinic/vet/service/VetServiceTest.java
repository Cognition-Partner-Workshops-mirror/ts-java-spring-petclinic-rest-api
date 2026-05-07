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
    private VetResponseDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));
        vetDto = new VetResponseDto(1, "James", "Carter", List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void getVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void createVet_success() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.lastName()).isEqualTo("Carter");
    }

    @Test
    void createVet_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void createVet_specialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(99, "unknown")));

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createVet_withNullSpecialtyId() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(null, "radiology")));
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
    }

    @Test
    void updateVet_success() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto updatedDto = new VetResponseDto(1, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void updateVet_notFound() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsMatches() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsMatches() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }
}
