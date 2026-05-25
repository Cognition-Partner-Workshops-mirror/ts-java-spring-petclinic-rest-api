package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpecialtyServiceImpl} with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiologyEntity;
    private SpecialtyResponseDto radiologyResponse;
    private SpecialtyRequestDto radiologyRequest;

    @BeforeEach
    void setUp() {
        radiologyEntity = new Specialty(1, "radiology");
        radiologyResponse = new SpecialtyResponseDto(1, "radiology");
        radiologyRequest = new SpecialtyRequestDto("radiology");
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryResponse = new SpecialtyResponseDto(2, "surgery");

        when(specialtyRepository.findAll()).thenReturn(List.of(radiologyEntity, surgery));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(radiologyResponse, surgeryResponse));

        List<SpecialtyResponseDto> result = specialtyService.findAll();
        assertThat(result).hasSize(2);
        verify(specialtyRepository).findAll();
    }

    @Test
    void findById_shouldReturnSpecialty_whenExists() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = specialtyService.findById(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldThrowNotFound_whenDoesNotExist() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_shouldSaveAndReturnSpecialty() {
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.empty());
        when(specialtyMapper.toEntity(radiologyRequest)).thenReturn(radiologyEntity);
        when(specialtyRepository.save(radiologyEntity)).thenReturn(radiologyEntity);
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = specialtyService.create(radiologyRequest);
        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).save(radiologyEntity);
    }

    @Test
    void create_shouldThrowDuplicate_whenNameAlreadyExists() {
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiologyEntity));

        assertThatThrownBy(() -> specialtyService.create(radiologyRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");

        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void update_shouldModifyAndReturnSpecialty() {
        SpecialtyRequestDto updateRequest = new SpecialtyRequestDto("oncology");
        Specialty updatedEntity = new Specialty(1, "oncology");
        SpecialtyResponseDto updatedResponse = new SpecialtyResponseDto(1, "oncology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyRepository.findByNameIgnoreCase("oncology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(radiologyEntity)).thenReturn(updatedEntity);
        when(specialtyMapper.toResponseDto(updatedEntity)).thenReturn(updatedResponse);

        SpecialtyResponseDto result = specialtyService.update(1, updateRequest);
        assertThat(result.getName()).isEqualTo("oncology");
        verify(specialtyMapper).updateEntityFromDto(updateRequest, radiologyEntity);
    }

    @Test
    void update_shouldThrowNotFound_whenDoesNotExist() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, radiologyRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowDuplicate_whenNameConflictsWithAnother() {
        Specialty otherEntity = new Specialty(2, "radiology");
        SpecialtyRequestDto updateRequest = new SpecialtyRequestDto("radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(otherEntity));

        assertThatThrownBy(() -> specialtyService.update(1, updateRequest))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_shouldAllowSameName_whenUpdatingSameEntity() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiologyEntity));
        when(specialtyRepository.save(radiologyEntity)).thenReturn(radiologyEntity);
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = specialtyService.update(1, radiologyRequest);
        assertThat(result).isNotNull();
    }

    @Test
    void delete_shouldRemoveSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));

        specialtyService.delete(1);
        verify(specialtyRepository).delete(radiologyEntity);
    }

    @Test
    void delete_shouldThrowNotFound_whenDoesNotExist() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_shouldReturnMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad"))
            .thenReturn(List.of(radiologyEntity));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(radiologyResponse));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_shouldReturnEmptyList_whenNoMatch() {
        when(specialtyRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.searchByName("xyz");
        assertThat(result).isEmpty();
    }
}
