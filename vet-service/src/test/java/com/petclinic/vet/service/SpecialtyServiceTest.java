package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
    private SpecialtyService service;

    private Specialty entity;
    private SpecialtyResponse response;
    private SpecialtyRequest request;

    @BeforeEach
    void setUp() {
        entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        response = new SpecialtyResponse(1, "radiology");
        request = new SpecialtyRequest("radiology");
    }

    @Test
    void findAll_returnsList() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponseList(List.of(entity))).thenReturn(List.of(response));

        List<SpecialtyResponse> result = service.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_found() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.findById(1);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_savesAndReturns() {
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.create(request);
        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(entity);
    }

    @Test
    void update_found_updatesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.update(1, request);
        assertThat(result.name()).isEqualTo("radiology");
        verify(mapper).updateEntity(request, entity);
    }

    @Test
    void update_notFound_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_found_deletesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.delete(1);
        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(entity);
    }

    @Test
    void delete_notFound_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsList() {
        when(repository.findByNameContainingIgnoreCase("rad")).thenReturn(List.of(entity));
        when(mapper.toResponseList(List.of(entity))).thenReturn(List.of(response));

        List<SpecialtyResponse> result = service.searchByName("rad");
        assertThat(result).hasSize(1);
    }
}
