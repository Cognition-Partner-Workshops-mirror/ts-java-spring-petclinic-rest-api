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
    private SpecialtyService service;

    private Specialty entity;
    private SpecialtyResponseDto responseDto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        entity = new Specialty(1, "radiology");
        responseDto = new SpecialtyResponseDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
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
    void getById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_savesAndReturns() {
        when(mapper.toEntity(requestDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.create(requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(entity);
    }

    @Test
    void update_modifiesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

        SpecialtyResponseDto result = service.update(1, requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(mapper).updateEntity(requestDto, entity);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
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
    void delete_throwsWhenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
