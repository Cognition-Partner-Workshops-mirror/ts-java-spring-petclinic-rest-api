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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
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

    private Vet james;
    private VetResponseDto jamesDto;

    @BeforeEach
    void setUp() {
        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");

        jamesDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_shouldReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
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
        SpecialtyResponseDto radiologyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(radiologyDto));

        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Vet newVet = new Vet();
        newVet.setFirstName("Helen");
        newVet.setLastName("Leary");

        Vet savedVet = new Vet();
        savedVet.setId(7);
        savedVet.setFirstName("Helen");
        savedVet.setLastName("Leary");

        VetResponseDto savedDto = new VetResponseDto(7, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIn(Set.of("radiology"))).thenReturn(List.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getFirstName()).isEqualTo("Helen");
    }

    @Test
    void create_shouldHandleNoSpecialties() {
        VetRequestDto request = new VetRequestDto("John", "Doe", new ArrayList<>());
        Vet newVet = new Vet();
        newVet.setFirstName("John");
        newVet.setLastName("Doe");

        Vet savedVet = new Vet();
        savedVet.setId(8);
        savedVet.setFirstName("John");
        savedVet.setLastName("Doe");

        VetResponseDto savedDto = new VetResponseDto(8, "John", "Doe", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(8);
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_shouldHandleNullSpecialties() {
        VetRequestDto request = new VetRequestDto("John", "Doe", null);
        Vet newVet = new Vet();
        newVet.setFirstName("John");
        newVet.setLastName("Doe");

        Vet savedVet = new Vet();
        savedVet.setId(9);
        savedVet.setFirstName("John");
        savedVet.setLastName("Doe");

        VetResponseDto savedDto = new VetResponseDto(9, "John", "Doe", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(9);
    }

    @Test
    void update_shouldUpdateAndReturnVet() {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", new ArrayList<>());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(new VetResponseDto(1, "Updated", "Carter", List.of()));

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        VetRequestDto request = new VetRequestDto("X", "Y", new ArrayList<>());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldResolveSpecialties() {
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(surgeryDto));

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findByNameIn(Set.of("surgery"))).thenReturn(List.of(surgery));
        when(vetRepository.save(james)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of(surgeryDto)));

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_shouldDeleteVet() {
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
    void findBySpecialty_shouldReturnMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_shouldReturnMatchingVets() {
        when(vetRepository.findByNameContainingIgnoreCase("james")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.searchByName("james");

        assertThat(result).hasSize(1);
    }
}
