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
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for VetServiceImpl using Mockito mocks.
 * Tests business logic including specialty resolution without database.
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

    private Vet james;
    private VetResponseDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>());

        jamesDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_shouldReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_shouldPersistAndReturnVet() {
        SpecialtyResponseDto radDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(radDto));

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        Vet savedHelen = new Vet();
        savedHelen.setId(2);
        savedHelen.setFirstName("Helen");
        savedHelen.setLastName("Leary");
        savedHelen.setSpecialties(Set.of(radiology));

        VetResponseDto helenDto = new VetResponseDto(2, "Helen", "Leary", List.of(radDto));

        when(vetMapper.toEntity(request)).thenReturn(helen);
        when(specialtyRepository.findByNameInIgnoreCase(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(helen)).thenReturn(savedHelen);
        when(vetMapper.toResponseDto(savedHelen)).thenReturn(helenDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getFirstName()).isEqualTo("Helen");
        verify(vetRepository).save(helen);
    }

    @Test
    void create_withNoSpecialties_shouldCreateVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Vet savedVet = new Vet();
        savedVet.setId(1);
        savedVet.setFirstName("James");
        savedVet.setLastName("Carter");
        savedVet.setSpecialties(new HashSet<>());

        when(vetMapper.toEntity(request)).thenReturn(vet);
        when(vetRepository.save(vet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_shouldCreateVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Vet savedVet = new Vet();
        savedVet.setId(1);
        savedVet.setFirstName("James");
        savedVet.setLastName("Carter");
        savedVet.setSpecialties(new HashSet<>());

        when(vetMapper.toEntity(request)).thenReturn(vet);
        when(vetRepository.save(vet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(jamesDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void update_shouldModifyAndReturnVet() {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of());
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getLastName()).isEqualTo("Updated");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        VetRequestDto request = new VetRequestDto("Test", "Test", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));

        vetService.delete(1);

        verify(vetRepository).delete(james);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_shouldDelegateToRepository() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_shouldDelegateToRepository() {
        when(vetRepository.findByNameContaining("jam")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(List.of(james))).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.searchByName("jam");

        assertThat(result).hasSize(1);
    }
}
