package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponseList(any())).thenReturn(List.of(response));

        List<SpecialtyResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
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
    void update_updatesAndReturns() {
        SpecialtyRequest updateRequest = new SpecialtyRequest("oncology");
        SpecialtyResponse updatedResponse = new SpecialtyResponse(1, "oncology");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(updatedResponse);

        SpecialtyResponse result = service.update(1, updateRequest);

        assertThat(result.name()).isEqualTo("oncology");
        verify(mapper).updateEntity(updateRequest, entity);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        SpecialtyResponse result = service.delete(1);

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
