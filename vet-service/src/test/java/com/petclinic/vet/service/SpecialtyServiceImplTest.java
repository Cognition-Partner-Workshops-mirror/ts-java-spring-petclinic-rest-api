package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
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
        entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        dto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<SpecialtyDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existing_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        SpecialtyDto result = service.findById(1);

        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExisting_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyDto input = new SpecialtyDto(null, "oncology");
        Specialty newEntity = new Specialty();
        newEntity.setName("oncology");

        when(mapper.toEntity(input)).thenReturn(newEntity);
        when(repository.save(any(Specialty.class))).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        SpecialtyDto result = service.create(input);

        assertThat(result).isNotNull();
        verify(repository).save(any(Specialty.class));
    }

    @Test
    void update_existing_updatesAndReturns() {
        SpecialtyDto updateDto = new SpecialtyDto(1, "updated");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new SpecialtyDto(1, "updated"));

        SpecialtyDto result = service.update(1, updateDto);

        assertThat(result.name()).isEqualTo("updated");
        verify(mapper).updateEntity(updateDto, entity);
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, dto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_deletesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        SpecialtyDto result = service.delete(1);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).delete(entity);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
