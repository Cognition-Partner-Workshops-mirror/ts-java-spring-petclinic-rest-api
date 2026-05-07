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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        james = new Vet(1, "James", "Carter");
        jamesDto = new VetResponseDto(1, "James", "Carter", new ArrayList<>());
        radiology = new Specialty(1, "radiology");
    }

    @Test
    void findAll_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(jamesDto));

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
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_shouldSaveVetWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", new ArrayList<>());
        Vet newVet = new Vet(null, "New", "Vet");
        Vet saved = new Vet(7, "New", "Vet");
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet", new ArrayList<>());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(7);
        verify(vetRepository).save(newVet);
    }

    @Test
    void create_shouldSaveVetWithSpecialties() {
        List<SpecialtyResponseDto> specDtos = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto request = new VetRequestDto("Helen", "Leary", specDtos);
        Vet newVet = new Vet(null, "Helen", "Leary");
        Vet saved = new Vet(2, "Helen", "Leary");
        saved.setSpecialties(Set.of(radiology));
        VetResponseDto savedDto = new VetResponseDto(2, "Helen", "Leary", specDtos);

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void create_shouldThrowWhenSpecialtyNotFound() {
        List<SpecialtyResponseDto> specDtos = List.of(new SpecialtyResponseDto(999, "unknown"));
        VetRequestDto request = new VetRequestDto("Test", "Vet", specDtos);
        Vet newVet = new Vet(null, "Test", "Vet");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateVetFields() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", new ArrayList<>());
        Vet updated = new Vet(1, "Updated", "Name");
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name", new ArrayList<>());

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetRepository.save(james)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void update_shouldUpdateVetWithSpecialties() {
        List<SpecialtyResponseDto> specDtos = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto request = new VetRequestDto("James", "Carter", specDtos);
        Vet updated = new Vet(1, "James", "Carter");
        updated.setSpecialties(Set.of(radiology));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Carter", specDtos);

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, new VetRequestDto("x", "y", new ArrayList<>())))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveAndReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_shouldReturnVetsWithSpecialty() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_shouldReturnMatchingVets() {
        when(vetRepository.findByNameContainingIgnoreCase("James")).thenReturn(List.of(james));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(jamesDto));

        List<VetResponseDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_shouldHandleNullSpecialtiesList() {
        VetRequestDto request = new VetRequestDto("Test", "Vet", null);
        Vet newVet = new Vet(null, "Test", "Vet");
        Vet saved = new Vet(8, "Test", "Vet");
        VetResponseDto savedDto = new VetResponseDto(8, "Test", "Vet", new ArrayList<>());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(8);
    }

    @Test
    void create_shouldHandleSpecialtyWithNullId() {
        List<SpecialtyResponseDto> specDtos = List.of(new SpecialtyResponseDto(null, "radiology"));
        VetRequestDto request = new VetRequestDto("Test", "Vet", specDtos);
        Vet newVet = new Vet(null, "Test", "Vet");
        Vet saved = new Vet(9, "Test", "Vet");
        VetResponseDto savedDto = new VetResponseDto(9, "Test", "Vet", new ArrayList<>());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getId()).isEqualTo(9);
    }
}
