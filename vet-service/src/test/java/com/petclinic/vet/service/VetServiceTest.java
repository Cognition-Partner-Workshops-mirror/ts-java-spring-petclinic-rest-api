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
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));

        vetResponseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void getAllVets_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVetById_shouldReturnVetWhenFound() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void getVetById_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void createVet_shouldSaveAndReturnVet() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(requestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void createVet_shouldThrowWhenSpecialtyNotFound() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(99, "nonexistent")));

        Vet newVet = new Vet();
        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createVet_withEmptySpecialties_shouldSucceed() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of());

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        VetResponseDto responseWithNoSpecialties = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseWithNoSpecialties);

        VetResponseDto result = vetService.createVet(requestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void createVet_withNullSpecialtyId_shouldSkip() {
        SpecialtyResponseDto dtoWithNullId = new SpecialtyResponseDto(null, "radiology");
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of(dtoWithNullId));

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(requestDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(requestDto);

        assertThat(result).isNotNull();
    }

    @Test
    void updateVet_shouldUpdateAndReturnVet() {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, requestDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_shouldThrowWhenNotFound() {
        VetRequestDto requestDto = new VetRequestDto("Test", "Test", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_shouldDeleteAndReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_shouldReturnMatchingVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_shouldReturnMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_shouldReturnMatchingVets() {
        when(vetRepository.findByNameContainingIgnoreCase("james")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.searchByName("james");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_shouldReturnEmptyForNoMatch() {
        when(vetRepository.findByNameContainingIgnoreCase("unknown")).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByName("unknown");

        assertThat(result).isEmpty();
    }
}
