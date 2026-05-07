package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
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
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty specialty;
    private SpecialtyResponseDto responseDto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        responseDto = new SpecialtyResponseDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
    }

    @Test
    void listSpecialties_returnsAll() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.getSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void addSpecialty_createsAndReturns() {
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("radiology");
        when(specialtyMapper.toEntity(requestDto)).thenReturn(newSpecialty);
        when(specialtyRepository.save(newSpecialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.addSpecialty(requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyRepository).save(newSpecialty);
    }

    @Test
    void updateSpecialty_updatesAndReturns() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyMapper).updateEntity(requestDto, specialty);
    }

    @Test
    void updateSpecialty_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_deletesAndReturns() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of());
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void deleteSpecialty_removesSpecialtyFromAssociatedVets() {
        Vet vet = new Vet(1, "James", "Carter");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(specialty);
        vet.setSpecialties(specialties);

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        specialtyService.deleteSpecialty(1);

        assertThat(vet.getSpecialties()).doesNotContain(specialty);
        verify(vetRepository).saveAll(List.of(vet));
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void deleteSpecialty_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
    }
}
