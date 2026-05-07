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
import static org.mockito.ArgumentMatchers.anyList;
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
    private Specialty specialty;
    private VetResponseDto vetResponseDto;
    private VetRequestDto vetRequestDto;
    private SpecialtyResponseDto specialtyResponseDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(specialty));

        specialtyResponseDto = new SpecialtyResponseDto(1, "radiology");
        vetResponseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyResponseDto));
        vetRequestDto = new VetRequestDto("James", "Carter", List.of(specialtyResponseDto));
    }

    @Test
    void listVets_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getVet_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void addVet_createsVet() {
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        when(vetMapper.toEntity(vetRequestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.addVet(vetRequestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void addVet_throwsWhenSpecialtyNotFound() {
        Vet newVet = new Vet();
        when(vetMapper.toEntity(vetRequestDto)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.addVet(vetRequestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void addVet_withEmptySpecialties() {
        VetRequestDto noSpecialtiesDto = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        when(vetMapper.toEntity(noSpecialtiesDto)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(new VetResponseDto(1, "James", "Carter", List.of()));

        VetResponseDto result = vetService.addVet(noSpecialtiesDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void updateVet_updatesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.updateVet(1, vetRequestDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void updateVet_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, vetRequestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateVet_throwsWhenSpecialtyNotFound() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(1, vetRequestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void deleteVet_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetResponseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
