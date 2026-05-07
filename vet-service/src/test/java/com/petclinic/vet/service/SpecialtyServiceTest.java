package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty specialty;
    private SpecialtyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(Instant.now());
        specialty.setUpdatedAt(Instant.now());
        responseDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void listSpecialties_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(List.of(specialty))).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.getSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_nonExistingId_throwsNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void createSpecialty_validRequest_returnsCreated() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("oncology");
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("oncology");

        when(specialtyMapper.toEntity(requestDto)).thenReturn(newSpecialty);
        when(specialtyRepository.save(newSpecialty)).thenReturn(newSpecialty);
        when(specialtyMapper.toResponseDto(newSpecialty)).thenReturn(new SpecialtyResponseDto(4, "oncology"));

        SpecialtyResponseDto result = specialtyService.createSpecialty(requestDto);

        assertThat(result.name()).isEqualTo("oncology");
        verify(specialtyRepository).save(newSpecialty);
    }

    @Test
    void updateSpecialty_existingId_returnsUpdated() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(new SpecialtyResponseDto(1, "updated"));

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, requestDto);

        assertThat(result.name()).isEqualTo("updated");
        verify(specialtyMapper).updateEntity(eq(requestDto), eq(specialty));
    }

    @Test
    void updateSpecialty_nonExistingId_throwsNotFound() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_existingId_returnsDeleted() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void deleteSpecialty_nonExistingId_throwsNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_matchingName_returnsResults() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(List.of(specialty))).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
    }
}
