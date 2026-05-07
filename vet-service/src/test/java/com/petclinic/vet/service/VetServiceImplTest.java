package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private Vet vetEntity;
    private Specialty specialtyEntity;
    private VetDto vetDto;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(Set.of(specialtyEntity));

        vetDto = new VetDto(1, "James", "Carter", List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listAll_returnsMappedDtos() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void getById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_withSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetMapper.toEntity(eq(request), any())).thenReturn(vetEntity);
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vetEntity);
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetDto newDto = new VetDto(2, "James", "Carter", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toDto(newVet)).thenReturn(newDto);

        VetDto result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetDto newDto = new VetDto(2, "James", "Carter", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toDto(newVet)).thenReturn(newDto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialtyId() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(null, "radiology")));
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetDto newDto = new VetDto(2, "James", "Carter", List.of());

        when(vetMapper.toEntity(eq(request), any())).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toDto(newVet)).thenReturn(newDto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withInvalidSpecialtyId_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(99, "nonexistent")));

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void update_found() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        VetDto updatedDto = new VetDto(1, "Helen", "Leary", List.of(new SpecialtyDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(updatedDto);

        VetDto result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
        verify(vetMapper).updateEntity(eq(request), eq(vetEntity), any());
    }

    @Test
    void update_notFound() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_delegatesToRepository() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_delegatesToRepository() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyAndLastName_delegatesToRepository() {
        when(vetRepository.findBySpecialtyNameAndLastNameContainingIgnoreCase("radiology", "Carter"))
            .thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findBySpecialtyAndLastName("radiology", "Carter");

        assertThat(result).hasSize(1);
    }
}
