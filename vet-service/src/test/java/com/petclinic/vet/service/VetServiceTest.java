package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl service;

    private Vet vet;
    private VetResponseDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));
        vetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_validDto_createsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties_createsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_createsVet() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet noSpecVet = new Vet(2, "James", "Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_invalidSpecialtyId_throwsException() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_existingId_updatesVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        VetResponseDto updatedDto = new VetResponseDto(1, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedDto);

        VetResponseDto result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_nonExistingId_throwsException() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());

        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameAndSpecialtyName_returnsMatchingVets() {
        when(vetRepository.findByLastNameAndSpecialtyName("Carter", "radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = service.findByLastNameAndSpecialtyName("Carter", "radiology");

        assertThat(result).hasSize(1);
    }
}
