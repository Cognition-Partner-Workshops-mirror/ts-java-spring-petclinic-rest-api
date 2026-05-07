package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
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
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Vet vet;
    private VetDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");

        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));

        vetDto = new VetDto(1, "James", "Carter", List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void findAll_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_shouldReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.findById(1);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_shouldSaveAndReturnVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(new SpecialtyDto(1, "radiology")));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_shouldHandleEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_shouldHandleNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_shouldUpdateAndReturnVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(new SpecialtyDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        doNothing().when(vetMapper).updateEntity(request, vet);
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(new VetDto(1, "Helen", "Leary", List.of(new SpecialtyDto(1, "radiology"))));

        VetDto result = vetService.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));

        vetService.delete(1);

        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_shouldReturnMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialty_shouldReturnMatchingVets() {
        when(vetRepository.findBySpecialtyNameContaining("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void search_shouldReturnMatchingVets() {
        when(vetRepository.findByNameContaining("James")).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.search("James");

        assertThat(result).hasSize(1);
    }
}
