package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
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
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl service;

    private Vet entity;
    private VetDto dto;

    @BeforeEach
    void setUp() {
        entity = new Vet(1, "James", "Carter");
        dto = new VetDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(entity));
        when(vetMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<VetDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(entity));
        when(vetMapper.toDto(entity)).thenReturn(dto);

        VetDto result = service.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_withoutSpecialties() {
        VetDto inputDto = new VetDto(null, "Helen", "Leary", List.of());
        Vet newEntity = new Vet(null, "Helen", "Leary");
        Vet savedEntity = new Vet(2, "Helen", "Leary");
        VetDto outputDto = new VetDto(2, "Helen", "Leary", List.of());

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(vetRepository.save(newEntity)).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(outputDto);

        VetDto result = service.create(inputDto);

        assertThat(result.getId()).isEqualTo(2);
    }

    @Test
    void create_withSpecialties() {
        SpecialtyDto specDto = new SpecialtyDto(1, "radiology");
        VetDto inputDto = new VetDto(null, "Helen", "Leary", List.of(specDto));
        Vet newEntity = new Vet(null, "Helen", "Leary");
        Specialty specEntity = new Specialty(1, "radiology");
        Vet savedEntity = new Vet(2, "Helen", "Leary");
        VetDto outputDto = new VetDto(2, "Helen", "Leary", List.of(specDto));

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specEntity));
        when(vetRepository.save(newEntity)).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(outputDto);

        VetDto result = service.create(inputDto);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void create_withInvalidSpecialty() {
        SpecialtyDto specDto = new SpecialtyDto(999, "unknown");
        VetDto inputDto = new VetDto(null, "Helen", "Leary", List.of(specDto));
        Vet newEntity = new Vet(null, "Helen", "Leary");

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(inputDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void update_existingVet() {
        VetDto updateDto = new VetDto(null, "Updated", "Name", List.of());
        Vet savedEntity = new Vet(1, "Updated", "Name");
        VetDto outputDto = new VetDto(1, "Updated", "Name", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(entity));
        when(vetRepository.save(entity)).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(outputDto);

        VetDto result = service.update(1, updateDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void update_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(vetRepository).delete(entity);
    }

    @Test
    void delete_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsList() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(entity));
        when(vetMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<VetDto> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsList() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(entity));
        when(vetMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<VetDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_withNullSpecialtiesList() {
        VetDto inputDto = new VetDto(null, "Helen", "Leary", null);
        Vet newEntity = new Vet(null, "Helen", "Leary");
        Vet savedEntity = new Vet(2, "Helen", "Leary");
        VetDto outputDto = new VetDto(2, "Helen", "Leary", List.of());

        when(vetMapper.toEntity(inputDto)).thenReturn(newEntity);
        when(vetRepository.save(newEntity)).thenReturn(savedEntity);
        when(vetMapper.toDto(savedEntity)).thenReturn(outputDto);

        VetDto result = service.create(inputDto);

        assertThat(result.getId()).isEqualTo(2);
    }
}
