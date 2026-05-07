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

    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toResponseDtoList(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_validDto_createsSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(repository.existsByNameIgnoreCase("surgery")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(surgery);
        when(repository.save(surgery)).thenReturn(surgery);
        when(mapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        SpecialtyResponseDto result = service.create(request);

        assertThat(result.name()).isEqualTo("surgery");
        verify(repository).save(any(Specialty.class));
    }

    @Test
    void create_duplicateName_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(repository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("radiology");
    }

    @Test
    void update_existingId_updatesSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.findByNameIgnoreCase("updated")).thenReturn(Optional.empty());
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponseDto(radiology)).thenReturn(updatedDto);

        SpecialtyResponseDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("updated");
        verify(mapper).updateEntity(request, radiology);
    }

    @Test
    void update_nonExistingId_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_duplicateName_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty(2, "surgery");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(surgery));

        assertThatThrownBy(() -> service.update(1, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_sameNameSameId_succeeds() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void delete_existingId_deletesSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
