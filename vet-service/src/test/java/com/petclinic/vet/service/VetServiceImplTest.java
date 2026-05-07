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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private Vet vet;
    private VetResponseDto vetDto;

    @BeforeEach
    void setUp() {
        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        vetDto = new VetResponseDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_vetWithoutSpecialties_createsSuccessfully() {
        VetRequestDto requestDto = new VetRequestDto("John", "Doe", new ArrayList<>());
        Vet newVet = new Vet();
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        VetResponseDto savedDto = new VetResponseDto(7, "John", "Doe", List.of());

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(requestDto);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void create_vetWithSpecialties_resolvesSpecialtiesByName() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto requestDto = new VetRequestDto("Jane", "Smith", List.of(specDto));
        Vet newVet = new Vet();
        newVet.setFirstName("Jane");
        newVet.setLastName("Smith");
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        Vet saved = new Vet();
        saved.setId(8);
        saved.setFirstName("Jane");
        saved.setLastName("Smith");
        VetResponseDto savedDto = new VetResponseDto(8, "Jane", "Smith", List.of(specDto));

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findByNameIn(any())).thenReturn(List.of(specialty));
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(requestDto);

        assertThat(result.getId()).isEqualTo(8);
        verify(specialtyRepository).findByNameIn(any());
    }

    @Test
    void create_vetWithNullSpecialties_createsSuccessfully() {
        VetRequestDto requestDto = new VetRequestDto("John", "Doe", null);
        Vet newVet = new Vet();
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        Vet saved = new Vet();
        saved.setId(9);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        VetResponseDto savedDto = new VetResponseDto(9, "John", "Doe", List.of());

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(requestDto);

        assertThat(result.getId()).isEqualTo(9);
    }

    @Test
    void update_existingId_updatesFieldsAndReturns() {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Name", new ArrayList<>());
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetRepository.save(vet)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, requestDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("Name");
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, new VetRequestDto("a", "b", Collections.emptyList())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_withSpecialties_resolvesSpecialties() {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto requestDto = new VetRequestDto("Updated", "Name", List.of(specDto));
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name", List.of(specDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIn(any())).thenReturn(List.of(specialty));
        when(vetRepository.save(vet)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, requestDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        verify(specialtyRepository).findByNameIn(any());
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));

        vetService.delete(1);

        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsFilteredVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsFilteredVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
    }
}
