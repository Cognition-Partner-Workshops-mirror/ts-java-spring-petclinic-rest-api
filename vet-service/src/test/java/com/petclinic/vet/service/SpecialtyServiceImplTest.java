package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
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

    private Specialty radiology;
    private SpecialtyDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        radiologyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = service.findById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        Specialty saved = new Specialty();
        saved.setId(2);
        saved.setName("dentistry");
        SpecialtyDto savedDto = new SpecialtyDto(2, "dentistry");

        when(mapper.toEntity(request)).thenReturn(dentistry);
        when(repository.save(dentistry)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        SpecialtyDto result = service.create(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("dentistry");
    }

    @Test
    void update_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toDto(radiology)).thenReturn(new SpecialtyDto(1, "updated"));

        SpecialtyDto result = service.update(1, request);

        assertThat(result.getName()).isEqualTo("updated");
        verify(mapper).updateEntity(request, radiology);
    }

    @Test
    void update_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, new SpecialtyRequestDto("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = service.delete(1);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(repository).delete(radiology);
    }

    @Test
    void delete_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        when(repository.findByNameContainingIgnoreCase("rad")).thenReturn(List.of(radiology));
        when(mapper.toDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = service.searchByName("rad");

        assertThat(result).hasSize(1);
    }
}
