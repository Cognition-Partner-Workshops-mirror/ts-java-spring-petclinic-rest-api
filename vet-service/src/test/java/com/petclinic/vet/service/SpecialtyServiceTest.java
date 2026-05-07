package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import java.util.Optional;
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
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl service;

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
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(specialty));
        when(mapper.toResponseDto(specialty)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(mapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void create_savesAndReturnsSpecialty() {
        when(mapper.toEntity(requestDto)).thenReturn(specialty);
        when(repository.save(specialty)).thenReturn(specialty);
        when(mapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.create(requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(specialty);
    }

    @Test
    void update_existingId_updatesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(repository.save(specialty)).thenReturn(specialty);
        when(mapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.update(1, requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(mapper).updateEntity(requestDto, specialty);
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(repository.existsById(1)).thenReturn(true);

        service.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(repository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(repository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(specialty));
        when(mapper.toResponseDto(specialty)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = service.searchByName("radio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }
}
