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
    private VetResponseDto vetResponseDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));
        vetResponseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void getAllVets_returnsAll() {
        List<Vet> vets = List.of(vet);
        when(vetRepository.findAll()).thenReturn(vets);
        when(vetMapper.toResponseDtoList(vets)).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVetById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void getVetById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void createVet_success() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void createVet_specialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(99, "unknown")));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createVet_emptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet(null, "James", "Carter");
        Vet saved = new Vet(2, "James", "Carter");
        VetResponseDto responseDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void updateVet_success() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(
            new VetResponseDto(1, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology"))));

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Helen");
        assertThat(result.getLastName()).isEqualTo("Leary");
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
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        List<Vet> vets = List.of(vet);
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(vets);
        when(vetMapper.toResponseDtoList(vets)).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatches() {
        List<Vet> vets = List.of(vet);
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(vets);
        when(vetMapper.toResponseDtoList(vets)).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void createVet_nullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet(null, "James", "Carter");
        Vet saved = new Vet(3, "James", "Carter");
        VetResponseDto responseDto = new VetResponseDto(3, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(responseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getId()).isEqualTo(3);
    }
}
