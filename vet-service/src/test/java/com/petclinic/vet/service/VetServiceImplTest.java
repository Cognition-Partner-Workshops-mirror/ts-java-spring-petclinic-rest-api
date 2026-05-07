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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private Specialty radiology;
    private VetResponseDto vetDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));

        vetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_shouldReturnAll() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_shouldReturnWhenFound() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void getVet_shouldThrowWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void createVet_shouldSaveAndReturn() {
        VetRequestDto request = new VetRequestDto("New", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.setSpecialties(Set.of(radiology));
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.id()).isEqualTo(7);
        assertThat(result.firstName()).isEqualTo("New");
    }

    @Test
    void createVet_shouldThrowWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("New", "Vet",
            List.of(new SpecialtyResponseDto(999, "nonexistent")));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void createVet_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of());
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void createVet_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", null);
        Vet saved = new Vet();
        saved.setId(7);
        saved.setFirstName("New");
        saved.setLastName("Vet");
        saved.setSpecialties(new HashSet<>());
        VetResponseDto savedDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(saved);
        when(vetMapper.toResponseDto(saved)).thenReturn(savedDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void updateVet_shouldUpdateWhenFound() {
        VetRequestDto request = new VetRequestDto("Updated", "Name",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        updated.setSpecialties(Set.of(radiology));
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_shouldThrowWhenNotFound() {
        VetRequestDto request = new VetRequestDto("U", "N", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_shouldDeleteWhenFound() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_shouldThrowWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_shouldReturnMatching() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_shouldReturnMatching() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_shouldReturnMatching() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
