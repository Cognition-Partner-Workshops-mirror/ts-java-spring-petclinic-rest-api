package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.exception.DuplicateResourceException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyService service;

    private SpecialtyEntity entity;
    private SpecialtyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        entity = new SpecialtyEntity();
        entity.setId(1);
        entity.setName("radiology");

        responseDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void listAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void listAll_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<SpecialtyResponseDto> result = service.listAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_throwsNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        SpecialtyEntity saved = new SpecialtyEntity();
        saved.setId(2);
        saved.setName("dentistry");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(2, "dentistry");

        when(repository.existsByNameIgnoreCase("dentistry")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(saved);
        when(repository.save(saved)).thenReturn(saved);
        when(mapper.toResponseDto(saved)).thenReturn(savedDto);

        SpecialtyResponseDto result = service.create(request);

        assertThat(result.id()).isEqualTo(2);
        assertThat(result.name()).isEqualTo("dentistry");
    }

    @Test
    void create_throwsDuplicateWhenNameExists() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");

        when(repository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("radiology");
    }

    @Test
    void update_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "surgery");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.findByNameIgnoreCase("surgery")).thenReturn(Optional.empty());
        doNothing().when(mapper).updateEntity(request, entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(updatedDto);

        SpecialtyResponseDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("surgery");
        verify(mapper).updateEntity(request, entity);
    }

    @Test
    void update_throwsNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_throwsDuplicateWhenNameConflicts() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyEntity other = new SpecialtyEntity();
        other.setId(2);
        other.setName("surgery");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> service.update(1, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_allowsSameNameOnSameEntity() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "radiology");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateEntity(request, entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(updatedDto);

        SpecialtyResponseDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void delete_removesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(entity);
    }

    @Test
    void delete_throwsNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
