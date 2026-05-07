package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository repository;
    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty specialty;
    private SpecialtyResponse specialtyResponse;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        specialtyResponse = new SpecialtyResponse(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(specialty));
        when(mapper.toResponseList(any())).thenReturn(List.of(specialtyResponse));

        List<SpecialtyResponse> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(mapper.toResponse(specialty)).thenReturn(specialtyResponse);

        SpecialtyResponse result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_nonExistingId_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id 999");
    }

    @Test
    void create_validRequest_returnsCreated() {
        SpecialtyRequest request = new SpecialtyRequest("oncology");

        Specialty newSpecialty = new Specialty();
        newSpecialty.setId(4);
        newSpecialty.setName("oncology");

        when(repository.existsByName("oncology")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(newSpecialty);
        when(repository.save(any(Specialty.class))).thenReturn(newSpecialty);
        when(mapper.toResponse(newSpecialty)).thenReturn(new SpecialtyResponse(4, "oncology"));

        SpecialtyResponse result = specialtyService.create(request);

        assertThat(result.name()).isEqualTo("oncology");
    }

    @Test
    void create_duplicateName_throwsDuplicate() {
        SpecialtyRequest request = new SpecialtyRequest("radiology");

        when(repository.existsByName("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void update_existingId_returnsUpdated() {
        SpecialtyRequest request = new SpecialtyRequest("surgery updated");

        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(repository.save(any(Specialty.class))).thenReturn(specialty);
        when(mapper.toResponse(any(Specialty.class))).thenReturn(new SpecialtyResponse(1, "surgery updated"));

        SpecialtyResponse result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("surgery updated");
        verify(mapper).updateEntity(request, specialty);
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        SpecialtyRequest request = new SpecialtyRequest("surgery");

        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(mapper.toResponse(specialty)).thenReturn(specialtyResponse);

        SpecialtyResponse result = specialtyService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(specialty);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
