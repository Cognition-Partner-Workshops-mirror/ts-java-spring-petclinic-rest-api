package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
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
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl service;

    private Specialty entity;
    private SpecialtyDto dto;

    @BeforeEach
    void setUp() {
        entity = new Specialty(1, "radiology");
        dto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<SpecialtyDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findById_found() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        SpecialtyDto result = service.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_savesAndReturns() {
        Specialty newEntity = new Specialty(null, "surgery");
        Specialty savedEntity = new Specialty(2, "surgery");
        SpecialtyDto inputDto = new SpecialtyDto(null, "surgery");
        SpecialtyDto outputDto = new SpecialtyDto(2, "surgery");

        when(mapper.toEntity(inputDto)).thenReturn(newEntity);
        when(repository.save(newEntity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(outputDto);

        SpecialtyDto result = service.create(inputDto);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("surgery");
    }

    @Test
    void update_existingSpecialty() {
        SpecialtyDto updateDto = new SpecialtyDto(null, "updated");
        Specialty savedEntity = new Specialty(1, "updated");
        SpecialtyDto outputDto = new SpecialtyDto(1, "updated");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(outputDto);

        SpecialtyDto result = service.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("updated");
        verify(mapper).updateEntity(updateDto, entity);
    }

    @Test
    void update_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(repository).delete(entity);
    }

    @Test
    void delete_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
