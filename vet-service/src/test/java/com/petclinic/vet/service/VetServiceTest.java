package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
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
    private VetResponseDto vetResponseDto;

    @BeforeEach
    void setUp() {
        vet = new Vet(1, "James", "Carter");
        vetResponseDto = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
    }

    @Test
    void listVets_returnsAll() {
        List<Vet> entities = List.of(vet);
        when(vetRepository.findAll()).thenReturn(entities);
        when(vetMapper.toResponseDtoList(entities)).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void getVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void createVet_withExistingSpecialty() {
        Specialty radiology = new Specialty(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyRequestDto("radiology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void createVet_withNewSpecialty() {
        Specialty newSpec = new Specialty(4, "oncology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyRequestDto("oncology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("oncology"))
            .thenReturn(Collections.emptyList());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(newSpec);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void createVet_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", Collections.emptyList());

        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
    }

    @Test
    void createVet_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
    }

    @Test
    void updateVet_success() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", Collections.emptyList());
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name", Collections.emptyList());

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99,
            new VetRequestDto("A", "B", Collections.emptyList())))
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
    void findBySpecialty_returnsResults() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsResults() {
        when(vetRepository.searchByName("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.searchByName("Carter");

        assertThat(result).hasSize(1);
    }
}
