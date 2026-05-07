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
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet carter;
    private VetResponseDto carterDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        carter = new Vet(1, "James", "Carter");
        carter.setSpecialties(new HashSet<>(Set.of(radiology)));
        carterDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(carter));
        when(vetMapper.toResponseDtoList(List.of(carter))).thenReturn(List.of(carterDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.lastName()).isEqualTo("Carter");
    }

    @Test
    void getVet_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet")
            .hasMessageContaining("999");
    }

    @Test
    void addVet_withExistingSpecialty_createsAndReturns() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyRequestDto("radiology")));

        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(carter);
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void addVet_withNewSpecialty_createsSpecialtyAndVet() {
        VetRequestDto request = new VetRequestDto("New", "Vet",
            List.of(new SpecialtyRequestDto("cardiology")));

        Specialty cardiology = new Specialty(4, "cardiology");

        when(specialtyRepository.findByNameContainingIgnoreCase("cardiology"))
            .thenReturn(Collections.emptyList());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(cardiology);
        when(vetRepository.save(any(Vet.class))).thenReturn(carter);
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void addVet_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", Collections.emptyList());
        VetResponseDto soloDto = new VetResponseDto(2, "Solo", "Vet", List.of());

        Vet solo = new Vet(2, "Solo", "Vet");
        when(vetRepository.save(any(Vet.class))).thenReturn(solo);
        when(vetMapper.toResponseDto(solo)).thenReturn(soloDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void addVet_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", null);
        VetResponseDto soloDto = new VetResponseDto(2, "Solo", "Vet", List.of());

        Vet solo = new Vet(2, "Solo", "Vet");
        when(vetRepository.save(any(Vet.class))).thenReturn(solo);
        when(vetMapper.toResponseDto(solo)).thenReturn(soloDto);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void updateVet_found_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("James", "Updated",
            List.of(new SpecialtyRequestDto("radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology"))
            .thenReturn(List.of(radiology));
        when(vetRepository.save(carter)).thenReturn(carter);
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.lastName()).isEqualTo("Carter");
    }

    @Test
    void updateVet_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999,
            new VetRequestDto("X", "Y", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_found_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(carter));
        when(vetMapper.toResponseDto(carter)).thenReturn(carterDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(carter);
    }

    @Test
    void deleteVet_notFound_throwsException() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsMatches() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(carter));
        when(vetMapper.toResponseDtoList(List.of(carter))).thenReturn(List.of(carterDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsMatches() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(carter));
        when(vetMapper.toResponseDtoList(List.of(carter))).thenReturn(List.of(carterDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(carter));
        when(vetMapper.toResponseDtoList(List.of(carter))).thenReturn(List.of(carterDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
