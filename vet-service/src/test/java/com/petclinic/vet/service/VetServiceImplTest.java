package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

    private Vet vetEntity;
    private VetDto vetDto;
    private Specialty specialtyEntity;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");
        specialtyEntity.setCreatedAt(Instant.now());
        specialtyEntity.setUpdatedAt(Instant.now());

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setCreatedAt(Instant.now());
        vetEntity.setUpdatedAt(Instant.now());
        vetEntity.setSpecialties(new HashSet<>());

        vetDto = new VetDto(1, "James", "Carter", List.of());
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existing_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.findById(1);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_withSpecialties_savesVet() {
        SpecialtyDto specDto = new SpecialtyDto(1, "radiology");
        VetDto input = new VetDto(null, "James", "Carter", List.of(specDto));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.create(input);

        assertThat(result).isNotNull();
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties_savesVet() {
        VetDto input = new VetDto(null, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.create(input);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialtyId_savesVet() {
        SpecialtyDto specDto = new SpecialtyDto(null, "new-specialty");
        VetDto input = new VetDto(null, "James", "Carter", List.of(specDto));

        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.create(input);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialtiesList_savesVet() {
        VetDto input = new VetDto(null, "James", "Carter", null);

        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.create(input);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withInvalidSpecialtyId_throwsNotFound() {
        SpecialtyDto specDto = new SpecialtyDto(999, "unknown");
        VetDto input = new VetDto(null, "James", "Carter", List.of(specDto));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(input))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_existing_updatesVet() {
        VetDto updateDto = new VetDto(1, "Helen", "Leary", List.of());

        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(updateDto);

        VetDto result = service.update(1, updateDto);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, vetDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_withSpecialties_resolvesSpecialties() {
        SpecialtyDto specDto = new SpecialtyDto(1, "radiology");
        VetDto updateDto = new VetDto(1, "Helen", "Leary", List.of(specDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toDto(vetEntity)).thenReturn(updateDto);

        VetDto result = service.update(1, updateDto);

        assertThat(result).isNotNull();
    }

    @Test
    void delete_existing_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toDto(vetEntity)).thenReturn(vetDto);

        VetDto result = service.delete(1);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsMatching() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatching() {
        when(vetRepository.findBySpecialtyName("radiology"))
            .thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameAndSpecialtyName_returnsMatching() {
        when(vetRepository.findByLastNameAndSpecialtyName("Carter", "radiology"))
            .thenReturn(List.of(vetEntity));
        when(vetMapper.toDtoList(List.of(vetEntity))).thenReturn(List.of(vetDto));

        List<VetDto> result = service.findByLastNameAndSpecialtyName("Carter", "radiology");

        assertThat(result).hasSize(1);
    }
}
